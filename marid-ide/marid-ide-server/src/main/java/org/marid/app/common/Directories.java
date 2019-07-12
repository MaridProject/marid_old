/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
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
