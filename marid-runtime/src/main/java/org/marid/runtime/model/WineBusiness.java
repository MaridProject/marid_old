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

import org.marid.io.MaridFiles;
import org.marid.runtime.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class WineBusiness implements HasMetaInfo, AutoCloseable {

  private final Path deployment;
  private final Path deps;
  private final Path resources;
  private final URLClassLoader classLoader;
  private final Class<?> entryPoint;
  private final MetaInfo metaInfo;
  private final LinkedHashMap<String, Winery> wineries = new LinkedHashMap<>();

  public WineBusiness(URL zipFile) throws IOException {
    final var name = name(zipFile);

    try {
      deployment = Files.createTempDirectory("deployment");

      try (final var is = new ZipInputStream(zipFile.openStream(), UTF_8)) {
        unpack(is);
      }

      resources = deployment.resolve("resources");
      deps = deployment.resolve("deps");

      validate();

      metaInfo = metaInfo(name);
      classLoader = classLoader();

      try (final var jarFile = new JarFile(deployment.resolve("bundle.jar").toString(), true)) {
        final var classSuffix = ".class";
        entryPoint = jarFile.stream()
            .filter(e -> !e.isDirectory())
            .filter(e -> e.getName().endsWith(classSuffix))
            .map(ZipEntry::getName)
            .filter(e -> !e.contains("$"))
            .map(e -> e.startsWith("/") ? e.substring(1) : e)
            .map(e -> e.replace('/', '.'))
            .map(e -> {
              try {
                return Class.forName(e, false, classLoader);
              } catch (ClassNotFoundException x) {
                throw new UncheckedIOException(new IOException(x));
              }
            })
            .filter(c -> Arrays.stream(c.getMethods()).anyMatch(m -> {
              if (!"main".equals(m.getName())) {
                return false;
              }
              if (!Modifier.isStatic(m.getModifiers())) {
                return false;
              }
              if (!Modifier.isPublic(m.getModifiers())) {
                return false;
              }
              if (m.getParameterCount() != 1) {
                return false;
              }
              return m.getParameterTypes()[0].equals(Context.class);
            }))
            .findFirst()
            .orElseThrow(() -> new StreamCorruptedException("No entry point in " + deployment));
      }
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

  private MetaInfo metaInfo(String name) throws IOException {
    final var props = new Properties();
    final var bundle = deployment.resolve("bundle.jar");
    final var propsFile = deployment.resolve("meta.properties");

    if (Files.isRegularFile(propsFile)) {
      try (final var reader = Files.newBufferedReader(propsFile, UTF_8)) {
        props.load(reader);
      }
    }

    return new MetaInfo(
        name,
        props.getProperty("title", ""),
        props.getProperty("icon", ""),
        props.getProperty("description"),
        props.getProperty("author", ""),
        props.getProperty("version"),
        Files.getLastModifiedTime(bundle).toInstant().toString()
    );
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

  @Override
  public MetaInfo getMetaInfo() {
    return metaInfo;
  }

  public Class<?> getEntryPoint() {
    return entryPoint;
  }

  public LinkedHashMap<String, Winery> getWineries() {
    return wineries;
  }

  @Override
  public String toString() {
    return getName();
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

  static String name(URL url) {
    var path = url.getPath();

    final int exclIndex = path.indexOf('!');
    if (exclIndex >= 0) {
      path = path.substring(exclIndex + 1);
    }

    if (path.startsWith("/")) {
      path = path.substring(1);
    }

    final var normalized = Path.of(path);
    final var file = normalized.getFileName().toString();

    if (!file.endsWith(".zip")) {
      throw new IllegalArgumentException(url.toString());
    }

    if (file.contains(":") || file.contains("/") || file.contains("\\") || file.contains(";")) {
      throw new IllegalArgumentException(url.toString());
    }

    return file.substring(0, file.length() - 4).replaceAll("\\s+", "_");
  }
}
