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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public interface FileUtils {

  static void deleteFile(@NotNull Path path, @Nullable Throwable exceptionToFill) throws IOException {
    Files.walkFileTree(path, new FileVisitor<>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        try {
          Files.deleteIfExists(file);
        } catch (Throwable e) {
          if (exceptionToFill != null) {
            exceptionToFill.addSuppressed(e);
          } else {
            throw e;
          }
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (exceptionToFill != null) {
          exceptionToFill.addSuppressed(new UncheckedIOException("Unable to visit " + file, exc));
        } else {
          throw exc;
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null) {
          if (exceptionToFill != null) {
            exceptionToFill.addSuppressed(exc);
          } else {
            throw exc;
          }
        }
        try {
          Files.deleteIfExists(dir);
        } catch (Throwable e) {
          if (exceptionToFill != null) {
            exceptionToFill.addSuppressed(e);
          } else {
            throw e;
          }
        }
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
