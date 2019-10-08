package org.marid.runtime.util;

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

import org.marid.runtime.AbstractCellar;
import org.marid.runtime.Deployment;
import org.marid.runtime.exception.DeploymentBuildException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.System.Logger.Level.INFO;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class DeploymentBuilder {

  private static final System.Logger LOGGER = System.getLogger(DeploymentBuilder.class.getName());
  private static final int BUFFER_SIZE = 0xFFFF;

  private final String name;
  private final LinkedList<URL> deps = new LinkedList<>();
  private final LinkedHashMap<List<String>, Input> classes = new LinkedHashMap<>();
  private final LinkedHashMap<List<String>, Input> resources = new LinkedHashMap<>();
  private final LinkedList<String> cellars = new LinkedList<>();

  public DeploymentBuilder(String name) {
    this.name = name;
  }

  public DeploymentBuilder addDependency(URL url) {
    deps.add(url);
    return this;
  }

  public DeploymentBuilder addClass(List<String> path, byte[] content) {
    classes.put(path, () -> new ByteArrayInputStream(content));
    return this;
  }

  public DeploymentBuilder addClass(List<String> path, Path file) {
    classes.put(path, () -> Files.newInputStream(file));
    return this;
  }

  public DeploymentBuilder addClass(List<String> path, File file) {
    classes.put(path, () -> new FileInputStream(file));
    return this;
  }

  public DeploymentBuilder addClass(List<String> path, URL url) {
    classes.put(path, url::openStream);
    return this;
  }

  public DeploymentBuilder addResource(List<String> path, byte[] content) {
    resources.put(path, () -> new ByteArrayInputStream(content));
    return this;
  }

  public DeploymentBuilder addResource(List<String> path, Path file) {
    resources.put(path, () -> Files.newInputStream(file));
    return this;
  }

  public DeploymentBuilder addResource(List<String> path, File file) {
    resources.put(path, () -> new FileInputStream(file));
    return this;
  }

  public DeploymentBuilder addResource(List<String> path, URL url) {
    resources.put(path, url::openStream);
    return this;
  }

  public DeploymentBuilder addCellar(String cellar) {
    cellars.add(cellar);
    return this;
  }

  public Deployment build(String... args) {
    try {
      final var tasks = new LinkedList<Future<Path>>();
      final var tempDir = Files.createTempDirectory("deploymentBuilder");
      final var pool = new ThreadPoolExecutor(0, 16, 1L, SECONDS, new SynchronousQueue<>(), new CallerRunsPolicy());
      try {
        final var deps = tempDir.resolve("deps");
        final var resources = tempDir.resolve("resources");
        final var classes = tempDir.resolve("classes");
        Files.createDirectory(deps);
        Files.createDirectory(resources);
        Files.createDirectory(classes);

        // copy dependencies
        for (final var dep : this.deps) {
          tasks.add(pool.submit(() -> {
            final var path = dep.getPath();
            final int lastSep = path.lastIndexOf('/');
            final var fileName = path.substring(lastSep + 1);
            final var target = deps.resolve(fileName);
            try (final var is = dep.openStream()) {
              Files.copy(is, target);
            }
            return target;
          }));
        }

        // copy classes
        for (final var entry : this.classes.entrySet()) {
          tasks.add(pool.submit(() -> {
            final var target = entry.getKey().stream().reduce(classes, Path::resolve, Path::resolve);
            if (target.startsWith(classes)) {
              Files.createDirectories(target.getParent());
              try (final var is = entry.getValue().inputStream()) {
                Files.copy(is, target);
              }
            } else {
              throw new IllegalArgumentException("Invalid class: " + entry.getKey().toString());
            }
            return target;
          }));
        }

        // create cellar entries
        final var metaInf = resources.resolve("META-INF");
        final var services = metaInf.resolve("services");
        Files.createDirectory(metaInf);
        Files.createDirectory(services);
        tasks.add(pool.submit(() -> {
          final var cellars = services.resolve(AbstractCellar.class.getName());
          Files.write(cellars, this.cellars, UTF_8);
          return cellars;
        }));

        // copy resources
        for (final var entry : this.resources.entrySet()) {
          tasks.add(pool.submit(() -> {
            final var target = entry.getKey().stream().reduce(resources, Path::resolve, Path::resolve);
            if (target.startsWith(resources)) {
              Files.createDirectories(target.getParent());
              try (final var is = entry.getValue().inputStream()) {
                Files.copy(is, target);
              }
            } else {
              throw new IllegalArgumentException("Invalid resource: " + entry.getKey().toString());
            }
            return target;
          }));
        }

        // join tasks
        final var exception = new DeploymentBuildException();
        for (final var task : tasks) {
          try {
            final var path = task.get();
            LOGGER.log(INFO, "Processed {0}", path);
          } catch (ExecutionException e) {
            exception.addSuppressed(e.getCause());
          } catch (Throwable e) {
            exception.addSuppressed(e);
          }
        }
        if (exception.getSuppressed().length > 0) {
          throw exception;
        }

        // create zip file
        final var outputZip = tempDir.resolve(name + ".zip");
        final var tempDirUri = tempDir.toUri();
        try (final var zos = new ZipOutputStream(Files.newOutputStream(outputZip), UTF_8)) {
          zos.setLevel(Deflater.BEST_COMPRESSION);
          try (final var stream = Files.walk(tempDir)) {
            final byte[] buffer = new byte[BUFFER_SIZE];
            final var crc = new CRC32();
            stream.filter(f -> !outputZip.equals(f)).forEach(file -> {
              final var relativePath = tempDirUri.relativize(file.toUri()).getPath();
              final var zipEntry = new ZipEntry(relativePath);
              try {
                zos.putNextEntry(zipEntry);
                if (Files.isRegularFile(file)) {
                  final var size = Files.size(file);
                  try (final var is = Files.newInputStream(file)) {
                    while (true) {
                      final int n = is.read(buffer);
                      if (n < 0) {
                        break;
                      }
                      crc.update(buffer, 0, n);
                      zos.write(buffer, 0, n);
                    }
                    zipEntry.setSize(size);
                    zipEntry.setLastModifiedTime(Files.getLastModifiedTime(file));
                    zipEntry.setCrc(crc.getValue());
                    crc.reset();
                  }
                }
                zos.closeEntry();
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            });
          }
        }

        return new Deployment(tempDir.toUri().toURL(), List.of(args));
      } finally {
        pool.shutdown();
        while (!pool.isTerminated()) {
          Thread.onSpinWait();
        }
        FileUtils.deleteFile(tempDir, null);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private interface Input {

    InputStream inputStream() throws IOException;
  }
}
