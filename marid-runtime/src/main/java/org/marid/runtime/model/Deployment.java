package org.marid.runtime.model;

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

import org.marid.runtime.exception.DeploymentCloseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

public final class Deployment implements AutoCloseable {

  private final Path deployment;
  private final Path deps;
  private final Path resources;
  private final URLClassLoader classLoader;

  public Deployment(URL zipFile) throws IOException {
    try {
      deployment = Files.createTempDirectory("marid");

      try (final var is = new ZipInputStream(zipFile.openStream(), StandardCharsets.UTF_8)) {
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
      try (final var reader = Files.newBufferedReader(propsFile, StandardCharsets.UTF_8)) {
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

  public void run(List<String> args) {
    Thread.currentThread().setContextClassLoader(classLoader);

    try (final var context = new Context(args)) {
      final LinkedHashSet<Class<? extends Rack>> types = ServiceLoader.load(Rack.class, classLoader).stream()
          .map(ServiceLoader.Provider::type)
          .collect(Collectors.toCollection(LinkedHashSet::new));
      for (final var type : types) {
        rack(context, types, type);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private Rack<?> rack(Context context, LinkedHashSet<Class<? extends Rack>> types, Class<? extends Rack> type, Class<?>... passed) {
    if (types.contains(type)) {
      return context.racks.computeIfAbsent(type, t -> create(context, types, type, passed));
    } else {
      return create(context, types, type, passed);
    }
  }

  private Rack<?> create(Context context, LinkedHashSet<Class<? extends Rack>> types, Class<? extends Rack> type, Class<?>... passed) {
    if (Arrays.stream(passed).anyMatch(e -> e == type)) {
      final var passedText = Arrays.stream(passed).map(Class::getName).collect(Collectors.joining(",", "[", "]"));
      throw new IllegalStateException("Circular dependency detected for " + type.getName() + ": " + passedText);
    }

    final var constructors = type.getConstructors();
    if (constructors.length != 1) {
      throw new IllegalStateException("Illegal rack " + type.getName() + ": # of constructors must be 1");
    }

    final var constructor = constructors[0];
    if (constructor.getParameterCount() < 1) {
      throw new IllegalStateException("Illegal rack " + type.getName() + ": # of constructor parameters must be >= 1");
    }

    final var argTypes = constructor.getParameterTypes();
    if (!Context.class.isAssignableFrom(argTypes[0])) {
      throw new IllegalStateException("Illegal rack " + type.getName() + ": the first parameter of constructor must be Context");
    }

    final var args = new Object[argTypes.length];
    args[0] = context;
    for (int i = 1; i < args.length; i++) {
      final var argType = argTypes[i];

      if (!Rack.class.isAssignableFrom(argType)) {
        throw new IllegalStateException("Illegal argument " + i + " of " + type);
      }

      final var newPassed = Arrays.copyOf(passed, passed.length + 1, Class[].class);
      newPassed[passed.length] = type;

      args[i] = rack(context, types, argType.asSubclass(Rack.class), newPassed);
    }

    try {
      return (Rack<?>) constructor.newInstance(args);
    } catch (InvocationTargetException e) {
      throw new IllegalStateException("Unable to call " + constructor, e.getTargetException());
    } catch (Throwable e) {
      throw new IllegalStateException("Unable to call " + constructor, e);
    }
  }

  @Override
  public void close() throws Exception {
    final var exception = new DeploymentCloseException(this);

    if (classLoader != null) {
      try {
        classLoader.close();
      } catch (Throwable e) {
        exception.addSuppressed(e);
      }
    }

    try {
      Files.walkFileTree(deployment, new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.deleteIfExists(file);
          return super.visitFile(file, attrs);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          Files.deleteIfExists(dir);
          return super.postVisitDirectory(dir, exc);
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
