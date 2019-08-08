/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

package org.marid.app.common;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Directories implements Closeable, InitializingBean {

  private final Path userHome;
  private final Path base;
  private final Path users;
  private final Path tempDir;
  private final Path rwtDir;

  @Autowired
  public Directories(@Value("${app.directory:${user.home}}") String appDirectory) throws Exception {
    this.userHome = Paths.get(appDirectory);
    this.base = userHome.resolve("marid-app");
    this.users = base.resolve("users");
    this.tempDir = Files.createTempDirectory("marid");
    this.rwtDir = Files.createTempDirectory("rwt");
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

  @Override
  public void close() throws IOException {
    try {
      FileSystemUtils.deleteRecursively(tempDir);
    } finally {
      FileSystemUtils.deleteRecursively(rwtDir);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Files.createDirectories(users);
  }
}
