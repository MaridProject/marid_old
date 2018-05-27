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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class RepositoriesDao {

  private final Path directory;

  public RepositoriesDao(UserDirectories directories) {
    directory = directories.getRepositoriesDirectory();
  }

  public List<String> repositoryNames() {
    try (final DirectoryStream<Path> files = Files.newDirectoryStream(directory, "*.properties")) {
      return StreamSupport.stream(files.spliterator(), false)
          .map(Path::getFileName)
          .map(Path::toString)
          .map(StringUtils::stripFilenameExtension)
          .collect(Collectors.toUnmodifiableList());
    } catch (NoSuchFileException x) {
      return List.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public void remove(String repository) {
    try {
      Files.deleteIfExists(directory.resolve(repository + ".properties"));
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }
}
