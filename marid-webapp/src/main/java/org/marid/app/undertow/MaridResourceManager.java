/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.app.undertow;

import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import org.marid.app.common.Directories;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class MaridResourceManager implements ResourceManager {

  private final Path rwtDir;
  private final Logger logger;

  public MaridResourceManager(Directories directories, Logger logger) {
    this.rwtDir = directories.getRwtDir();
    this.logger = logger;
  }

  @Override
  public Resource getResource(String path) throws IOException {
    final Resource resource;
    if (path.startsWith("/public/")) {
      final var classLoader = Thread.currentThread().getContextClassLoader();
      final var url = classLoader.getResource(path.substring(1));
      resource = url == null ? null : new URLResource(url, path);
    } else {
      final Path p = rwtDir.resolve(path.substring(1));
      if (p.startsWith(rwtDir) && Files.isRegularFile(p)) {
        resource = new URLResource(p.toUri().toURL(), path);
      } else {
        resource = null;
      }
    }
    if (resource == null) {
      logger.warn("Not found {}", path);
    }
    return resource;
  }

  @Override
  public boolean isResourceChangeListenerSupported() {
    return false;
  }

  @Override
  public void registerResourceChangeListener(ResourceChangeListener listener) {

  }

  @Override
  public void removeResourceChangeListener(ResourceChangeListener listener) {

  }

  @Override
  public void close() {
  }
}
