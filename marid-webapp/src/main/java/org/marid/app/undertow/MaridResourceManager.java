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

import io.undertow.server.handlers.resource.*;
import org.marid.app.common.Directories;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MaridResourceManager implements ResourceManager {

  private final Logger logger;
  private final PathResourceManager fileManager;

  public MaridResourceManager(Directories directories, Logger logger) {
    this.logger = logger;
    this.fileManager = new PathResourceManager(directories.getRwtDir(), 1024, true, false, false);
  }

  @Override
  public Resource getResource(String path) {
    final Resource resource;
    if (path.startsWith("/public/")) {
      final var classLoader = Thread.currentThread().getContextClassLoader();
      final var url = classLoader.getResource(path.substring(1));
      resource = url == null ? null : new URLResource(url, path);
    } else {
      resource = fileManager.getResource(path);
    }
    if (resource == null) {
      logger.warn("Not found {}", path);
    }
    return resource;
  }

  @Override
  public boolean isResourceChangeListenerSupported() {
    return fileManager.isResourceChangeListenerSupported();
  }

  @Override
  public void registerResourceChangeListener(ResourceChangeListener listener) {
    fileManager.registerResourceChangeListener(listener);
  }

  @Override
  public void removeResourceChangeListener(ResourceChangeListener listener) {
    fileManager.removeResourceChangeListener(listener);
  }

  @Override
  public void close() throws IOException {
    fileManager.close();
  }
}
