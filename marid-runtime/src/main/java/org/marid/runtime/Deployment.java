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
import org.marid.io.MaridFiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.lang.ref.Cleaner;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class Deployment implements AutoCloseable {

  private static final InheritableThreadLocal<Deployment> DEPLOYMENT = new InheritableThreadLocal<>();
  static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  private final Cleaner cleaner = Cleaner.create();
  private final Path deployment;
  private final Path deps;
  private final Path resources;
  private final Path classes;
  private final Path cellarsFile;
  private final URLClassLoader classLoader;
  private final LinkedHashMap<Class<? extends AbstractCellar>, AbstractCellar> cellars = new LinkedHashMap<>();
  private final Thread thread;
  private final LinkedTransferQueue<Command> queue = new LinkedTransferQueue<>();
  public final List<String> args;

  private volatile State state = State.NEW;
  private volatile Throwable startError;
  private volatile Throwable destroyError;

  public Deployment(URL zipFile, List<String> args) throws IOException {
    this.args = args;
    try {
      deployment = Files.createTempDirectory("marid");
      thread = new Thread(() -> {
        try {
          while (!Thread.currentThread().isInterrupted()) {
            final var task = queue.poll();
            if (task == null) {
              Thread.onSpinWait();
              continue;
            }
            switch (task) {
              case START:
                try {
                  run();
                } catch (Throwable e) {
                  startError = e;
                }
                break;
              case STOP:
                try {
                  destroy();
                } catch (Throwable e) {
                  destroyError = e;
                }
                break;
            }
          }
        } finally {
          destroy();
        }
      }, toString());
      cleaner.register(this, thread::interrupt);

      try (final var is = new ZipInputStream(zipFile.openStream(), UTF_8)) {
        unpack(is);
      }

      cellarsFile = deployment.resolve("cellars.list");
      classes = deployment.resolve("classes");
      resources = deployment.resolve("resources");
      deps = deployment.resolve("deps");

      validate();
      initialize();

      classLoader = classLoader();
    } catch (Throwable e) {
      try {
        destroy();
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

        if (target.equals(deployment)) {
          continue;
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
    if (!Files.isRegularFile(cellarsFile)) {
      throw new NullPointerException("cellarsFile does not exist");
    }
    Files.createDirectories(resources);
    Files.createDirectories(deps);
    if (!Files.isDirectory(classes)) {
      throw new FileNotFoundException(classes.toString());
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
    urls.add(classes.toUri().toURL());
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

  public void start() {
    switch (thread.getState()) {
      case NEW:
        thread.start();
        break;
      case TERMINATED:
        throw new IllegalStateException();
    }
    switch (state) {
      case STARTING:
      case RUNNING:
      case TERMINATING:
        return;
      case NEW:
      case TERMINATED:
        queue.add(Command.START);
        while (state != State.RUNNING && state != State.TERMINATED) {
          Thread.onSpinWait();
        }
        if (startError != null) {
          try {
            throw startError;
          } catch (RuntimeException | Error e) {
            throw e;
          } catch (Throwable e) {
            throw new IllegalStateException(e);
          } finally {
            startError = null;
          }
        }
        break;
    }
  }

  private void run() {
    if (state == State.STARTING || state == State.RUNNING) {
      return;
    }
    state = State.STARTING;
    Thread.currentThread().setContextClassLoader(classLoader);
    DEPLOYMENT.set(this);
    try (final var cellarClasses = Files.lines(cellarsFile, UTF_8)) {
      final var lines = cellarClasses
          .map(String::trim)
          .filter(l -> !l.startsWith("#") && !l.isEmpty())
          .collect(Collectors.toCollection(LinkedHashSet::new));
      for (final var line : lines) {
        final var cellarClass = classLoader.loadClass(line).asSubclass(AbstractCellar.class);
        final var providerMethod = publicLookup().findStatic(cellarClass, "provider", methodType(cellarClass));
        cellars.put(cellarClass, cellarClass.cast(providerMethod.invoke()));
      }
      state = State.RUNNING;
    } catch (Throwable e) {
      final var exception = new DeploymentStartException(this, e);
      try {
        destroy();
      } catch (Throwable ce) {
        exception.addSuppressed(ce);
      }
      throw exception;
    }
  }

  private void destroy() {
    if (state != State.RUNNING && state != State.STARTING) {
      return;
    }
    state = State.TERMINATING;
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
      MaridFiles.deleteRecursively(deployment);
    } catch (Throwable e) {
      exception.addSuppressed(e);
    }

    state = State.TERMINATED;

    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }

  @Override
  public void close() throws Exception {
    queue.add(Command.STOP);
    while (state != State.TERMINATED) {
      Thread.onSpinWait();
    }
    if (destroyError != null) {
      try {
        throw destroyError;
      } catch (Exception | Error e) {
        throw e;
      } catch (Throwable e) {
        throw new IllegalStateException(e);
      } finally {
        destroyError = null;
      }
    }
  }

  @Override
  public String toString() {
    return deployment.getFileName().toString();
  }

  public enum State {NEW, STARTING, RUNNING, TERMINATING, TERMINATED}
  public enum Command {START, STOP}
}
