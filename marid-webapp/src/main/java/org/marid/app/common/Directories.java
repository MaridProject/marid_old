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

package org.marid.app.common;

import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Directories {

  private final Path userHome;
  private final Path base;
  private final Path users;
  private final Path tempDir;
  private final Path rwtDir;

  public Directories() throws Exception {
    this.userHome = Paths.get(System.getProperty("user.home"));
    this.base = userHome.resolve("marid-app");
    this.users = base.resolve("users");
    this.tempDir = Files.createTempDirectory("marid");
    this.rwtDir = tempDir.resolve("rwt");
  }

  @PostConstruct
  private void createDirectoriesIfNecessary() throws IOException {
    Files.createDirectories(users);
  }

  @PreDestroy
  private void deleteTempDir() throws IOException {
    FileSystemUtils.deleteRecursively(tempDir);
  }

  public Path getUserHome() {
    return userHome;
  }

  public Path getBase() {
    return base;
  }

  public Path getUsers() {
    return users;
  }

  public Path getTempDir() {
    return tempDir;
  }

  public Path getRwtDir() {
    return rwtDir;
  }
}
