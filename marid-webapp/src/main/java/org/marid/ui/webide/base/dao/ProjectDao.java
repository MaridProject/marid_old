/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
package org.marid.ui.webide.base.dao;

import org.marid.ui.webide.base.UserDirectories;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.Files.walk;

@Component
public class ProjectDao {

  private final Path directory;

  public ProjectDao(UserDirectories userDirectories) {
    this.directory = userDirectories.getProjectsDirectory();
  }

  public List<String> getProjectNames() {
    try {
      return Files.list(directory)
          .filter(Files::isDirectory)
          .map(Path::getFileName)
          .map(Path::toString)
          .collect(Collectors.toUnmodifiableList());
    } catch (NoSuchFileException x) {
      return List.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public long getSize(String projectName) {
    try {
      return walk(directory.resolve(projectName))
          .filter(Files::isRegularFile)
          .mapToLong(f -> {
            try {
              return Files.size(f);
            } catch (NoSuchFileException x) {
              return 0L;
            } catch (IOException x) {
              throw new UncheckedIOException(x);
            }
          })
          .sum();
    } catch (NoSuchFileException x) {
      return 0L;
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public boolean exists(String projectName) {
    final Path path = directory.resolve(projectName);
    return Files.isDirectory(path);
  }

  public void tryCreate(String projectName) {
    try {
      final Path path = directory.resolve(projectName);
      Files.createDirectories(path);
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public boolean removeProject(String project) {
    try {
      final Path path = directory.resolve(project);
      if (Files.isDirectory(path)) {
        FileSystemUtils.deleteRecursively(path);
        return true;
      } else {
        return false;
      }
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }
}
