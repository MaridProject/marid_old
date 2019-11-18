/*-
 * #%L
 * marid-util
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
package org.marid.io;

import org.jetbrains.annotations.NotNull;
import org.marid.io.function.IOFunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.stream.Stream;

public interface MaridFiles {

  static long size(Path path) {
    if (Files.isDirectory(path)) {
      try (final DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
        long sum = 0L;
        for (final Path entry : stream) {
          sum += size(entry);
        }
        return sum;
      } catch (NotDirectoryException x) {
        return size(path);
      } catch (NoSuchFileException x) {
        return 0L;
      } catch (IOException x) {
        throw new UncheckedIOException(x);
      }
    } else if (Files.isRegularFile(path)) {
      try {
        return Files.size(path);
      } catch (NoSuchFileException x) {
        return 0L;
      } catch (IOException x) {
        throw new UncheckedIOException(x);
      }
    } else {
      return 0L;
    }
  }

  static void deleteRecursively(@NotNull Path path) {
    if (Files.isDirectory(path)) {
      try (final var stream = Files.newDirectoryStream(path)) {
        for (final var childPath : stream) {
          deleteRecursively(childPath);
        }
        Files.deleteIfExists(path);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } else {
      try {
        Files.deleteIfExists(path);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  static void delete(@NotNull Path path) {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static void deleteIfExists(@NotNull Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static <T> T getWithDirectoryStream(@NotNull Path path, @NotNull IOFunction<DirectoryStream<Path>, T> callback) {
    try (final var stream = Files.newDirectoryStream(path)) {
      return callback.applyChecked(stream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static <T> T getWithDirectoryStream(@NotNull Path path,
                                      @NotNull String glob,
                                      @NotNull IOFunction<DirectoryStream<Path>, T> callback) {
    try (final var stream = Files.newDirectoryStream(path, glob)) {
      return callback.applyChecked(stream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static <T> T getWithDirectoryStream(@NotNull Path path,
                                      @NotNull DirectoryStream.Filter<Path> filter,
                                      @NotNull IOFunction<DirectoryStream<Path>, T> callback) {
    try (final var stream = Files.newDirectoryStream(path, filter)) {
      return callback.applyChecked(stream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static Stream<Path> list(@NotNull Path path) {
    try {
      return Files.list(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static Stream<Path> walk(@NotNull Path path, int depth, @NotNull FileVisitOption... options) {
    try {
      return Files.walk(path, depth, options);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static long copy(@NotNull Path path, @NotNull OutputStream outputStream) {
    try {
      return Files.copy(path, outputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static long copy(@NotNull InputStream inputStream, @NotNull Path path, @NotNull CopyOption... options) {
    try {
      return Files.copy(inputStream, path, options);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static Path copy(@NotNull Path source, @NotNull Path target, @NotNull CopyOption... options) {
    try {
      return Files.copy(source, target, options);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static Path move(@NotNull Path source, @NotNull Path target, @NotNull CopyOption... options) {
    try {
      return Files.move(source, target, options);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static byte[] readAllBytes(@NotNull Path path) {
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static String readString(@NotNull Path path, @NotNull Charset charset) {
    try {
      return Files.readString(path, charset);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static List<String> readLines(@NotNull Path path, @NotNull Charset charset) {
    try {
      return Files.readAllLines(path, charset);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @NotNull
  static Stream<String> lines(@NotNull Path path, @NotNull Charset charset) {
    try {
      return Files.lines(path, charset);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static void createDirectory(@NotNull Path path, @NotNull FileAttribute<?>... attributes) {
    try {
      Files.createDirectory(path, attributes);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static void createDirectories(@NotNull Path path, @NotNull FileAttribute<?>... attributes) {
    try {
      Files.createDirectories(path, attributes);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
