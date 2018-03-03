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

package org.marid.app.dao;

import org.marid.app.common.Directories;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository
@SessionScope
public class CellarDao {

  private final Path userDir;
  private final Logger logger;

  @Autowired
  public CellarDao(Directories directories, Logger logger) {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    this.logger = logger;
    this.userDir = directories.getUsers().resolve(authentication.getName());
  }

  @PostConstruct
  private void createProfilesDirectoryIfNecessary() throws IOException {
    Files.createDirectories(userDir);

    logger.info("Session profiles: {}", userDir);
  }

  public Path getUserDir() {
    return userDir;
  }

  public NavigableSet<String> profileNames() {
    try {
      return Files.list(userDir)
          .filter(Files::isDirectory)
          .map(Path::getFileName)
          .map(Path::toString)
          .collect(Collectors.toCollection(TreeSet::new));
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }
}
