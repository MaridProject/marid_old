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

import org.marid.io.MaridFiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

public final class WineBusiness implements Runnable, AutoCloseable {

  private final String id;
  private final Path deployment;
  private final Path deps;
  private final Path resources;
  private final URLClassLoader classLoader;

  public WineBusiness(String id, URL zipFile) throws IOException {
    this.id = id;

    try {
      deployment = Files.createTempDirectory("marid");

      try (final var is = new ZipInputStream(zipFile.openStream(), StandardCharsets.UTF_8)) {
        unpack(is);
      }

      resources = deployment.resolve("resources");
      deps = deployment.resolve("deps");

      validate();

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
    return id;
  }

  @Override
  public void run() {
    final var services = ServiceLoader.load(EntryPoint.class, classLoader).stream()
        .map(ServiceLoader.Provider::get)
        .collect(Collectors.toList());

    final var context = new Context();

    Throwable exception = null;

    for (final var service : services) {
      try {
        service.run(context);
      } catch (Throwable e) {
        exception = e;
        break;
      }
    }

  }

  @Override
  public void close() throws Exception {
    final var exception = new Exception("Unable to close " + this);

    if (classLoader != null) {
      try {
        classLoader.close();
      } catch (Throwable e) {
        exception.addSuppressed(e);
      }
    }

    try {
      MaridFiles.delete(deployment);
    } catch (Throwable e) {
      exception.addSuppressed(e);
    }

    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }
}
