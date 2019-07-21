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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

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

  static void delete(Path path) throws IOException {
    Files.walkFileTree(path, new SimpleFileVisitor<>() {
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
  }
}
