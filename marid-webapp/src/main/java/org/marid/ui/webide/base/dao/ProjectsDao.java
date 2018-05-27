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

import org.marid.io.IOLongSupplier;
import org.marid.io.IOSupplier;
import org.marid.ui.webide.base.UserDirectories;
import org.marid.ui.webide.base.model.ProjectInfo;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProjectsDao {

  private final Path directory;

  public ProjectsDao(UserDirectories userDirectories) {
    this.directory = userDirectories.getProjectsDirectory();
  }

  public List<String> getProjectNames() {
    return IOSupplier.supply(() -> Files.list(directory)
        .filter(Files::isDirectory)
        .map(Path::getFileName)
        .map(Path::toString)
        .collect(Collectors.toList())
    );
  }

  public long getSize(String projectName) {
    return IOSupplier.supply(() -> Files.walk(directory.resolve(projectName))
        .filter(Files::isRegularFile)
        .mapToLong(p -> IOLongSupplier.supply(() -> Files.size(p)))
        .sum()
    );
  }

  public Optional<ProjectInfo> load(String projectName) {
    final Path path = directory.resolve(projectName);
    if (Files.isDirectory(path)) {
      final ProjectInfo projectInfo = new ProjectInfo();
      projectInfo.setName(projectName);
      return Optional.of(projectInfo);
    } else {
      return Optional.empty();
    }
  }

  public void saveOrModify(ProjectInfo projectInfo) {
    try {
      final Path path = directory.resolve(projectInfo.getName());
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
