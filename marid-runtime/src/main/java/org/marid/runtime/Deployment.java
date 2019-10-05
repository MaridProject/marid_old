package org.marid.runtime;

/*-
 * #%L
 * marid-runtime
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.marid.runtime.exception.CellarCloseException;
import org.marid.runtime.exception.DeploymentCloseException;
import org.marid.runtime.exception.DeploymentStartException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.UncheckedIOException;
import java.lang.ref.Cleaner;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class Deployment implements AutoCloseable {

  private static final InheritableThreadLocal<Deployment> DEPLOYMENT = new InheritableThreadLocal<>();
  static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  private final Cleaner cleaner = Cleaner.create();
  private final Path deployment;
  private final Path deps;
  private final Path resources;
  private final URLClassLoader classLoader;
  private final LinkedHashMap<Class<? extends AbstractCellar>, AbstractCellar> cellars = new LinkedHashMap<>();
  private final ThreadPoolExecutor executor;
  public final List<String> args;

  public Deployment(URL zipFile, List<String> args) throws IOException {
    this.args = args;
    try {
      deployment = Files.createTempDirectory("marid");
      executor = new ThreadPoolExecutor(1, 1, 0L, SECONDS, new SynchronousQueue<>(), r -> new Thread(r, getId()));
      cleaner.register(this, executor::shutdown);

      try (final var is = new ZipInputStream(zipFile.openStream(), UTF_8)) {
        unpack(is);
      }

      resources = deployment.resolve("resources");
      deps = deployment.resolve("deps");

      validate();
      initialize();

      classLoader = classLoader();
    } catch (Throwable e) {
      try {
        close();
      } catch (Throwable x) {
        e.addSuppressed(x);
      }
      throw e;
    }
  }

  private void unpack(ZipInputStream zipInputStream) throws IOException {
    for (var e = zipInputStream.getNextEntry(); e != null; e = zipInputStream.getNextEntry()) {
      try {
        final var target = deployment.resolve(e.getName());

        if (!target.startsWith(deployment)) {
          throw new StreamCorruptedException("Invalid entry: " + e.getName());
        }

        if (e.isDirectory()) {
          Files.createDirectory(target);
        } else {
          Files.copy(zipInputStream, target);
        }
        Files.setLastModifiedTime(target, e.getLastModifiedTime());
      } finally {
        zipInputStream.closeEntry();
      }
    }
  }

  private void validate() throws IOException {
    Files.createDirectories(resources);
    Files.createDirectories(deps);

    final var bundle = deployment.resolve("bundle.jar");
    if (!Files.isRegularFile(bundle)) {
      throw new FileNotFoundException(bundle.toString());
    }
  }

  private void initialize() throws IOException {
    final var propsFile = deployment.resolve("system.properties");
    if (Files.isRegularFile(propsFile)) {
      final var props = new Properties();
      try (final var reader = Files.newBufferedReader(propsFile, UTF_8)) {
        props.load(reader);
      }
      props.forEach(System.getProperties()::putIfAbsent);
    }
  }

  private URLClassLoader classLoader() throws IOException {
    final var urls = new ArrayList<URL>();
    urls.add(deployment.resolve("bundle.jar").toUri().toURL());
    urls.add(resources.toUri().toURL());
    try (final var dirStream = Files.newDirectoryStream(deps, "*.jar")) {
      for (final var path : dirStream) {
        urls.add(path.toUri().toURL());
      }
    }
    return new URLClassLoader(urls.toArray(URL[]::new), Thread.currentThread().getContextClassLoader());
  }

  public String getId() {
    return deployment.getFileName().toString();
  }

  public <C extends AbstractCellar> C get(Class<C> cellarClass) {
    return cellarClass.cast(cellars.get(cellarClass));
  }

  public static Deployment $() {
    return DEPLOYMENT.get();
  }

  public static Cleaner getCleaner() {
    return DEPLOYMENT.get().cleaner;
  }

  public static <C extends AbstractCellar> C $(Class<C> cellarClass) {
    return DEPLOYMENT.get().get(cellarClass);
  }

  void start() {
    executor.execute(this::run);
  }

  boolean stop() {
    try {
      executor.execute(this::close);
      return true;
    } catch (RejectedExecutionException e) {
      return false;
    }
  }

  private void run() {
    Thread.currentThread().setContextClassLoader(classLoader);
    DEPLOYMENT.set(this);
    try {
      for (final var cellar : ServiceLoader.load(AbstractCellar.class, classLoader)) {
        cellars.put(cellar.getClass(), cellar);
      }
    } catch (Throwable e) {
      final var exception = new DeploymentStartException(this, e);
      try {
        close();
      } catch (Throwable ce) {
        exception.addSuppressed(ce);
      }
      throw exception;
    }
    close();
  }

  @Override
  public void close() {
    final var exception = new DeploymentCloseException(this);

    final var entries = new LinkedList<>(cellars.entrySet());
    cellars.clear();
    for (final var it = entries.descendingIterator(); it.hasNext(); ) {
      final var entry = it.next();
      final var cellarClass = entry.getKey();
      final var cellarInstance = entry.getValue();
      try {
        cellarInstance.close();
      } catch (Throwable e) {
        exception.addSuppressed(new CellarCloseException(cellarClass, cellarInstance, e));
      } finally {
        it.remove();
      }
    }

    if (classLoader != null) {
      try {
        classLoader.close();
      } catch (Throwable e) {
        exception.addSuppressed(e);
      }
    }

    try {
      Files.walkFileTree(deployment, new FileVisitor<>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
          try {
            Files.deleteIfExists(file);
          } catch (Throwable e) {
            exception.addSuppressed(e);
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
          exception.addSuppressed(new UncheckedIOException("Unable to visit " + file, exc));
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
          if (exc != null) {
            exception.addSuppressed(exc);
          }
          try {
            Files.deleteIfExists(dir);
          } catch (Throwable e) {
            exception.addSuppressed(e);
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (Throwable e) {
      exception.addSuppressed(e);
    }

    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }

  @Override
  public String toString() {
    return deployment.toString();
  }
}
