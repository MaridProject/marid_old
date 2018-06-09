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

import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.URLResource;
import org.marid.app.common.Directories;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class MaridResourceManager extends ClassPathResourceManager {

  private final Path rwtDir;

  public MaridResourceManager(Directories directories) {
    super(Thread.currentThread().getContextClassLoader());
    this.rwtDir = directories.getRwtDir();
  }

  @Override
  public Resource getResource(String path) throws IOException {
    if (path.startsWith("/public/")) {
      return super.getResource(path);
    } else {
      final Path p = rwtDir.resolve(path.substring(1));
      if (p.startsWith(rwtDir) && Files.isRegularFile(p)) {
        return new URLResource(p.toUri().toURL(), path);
      } else {
        return null;
      }
    }
  }
}
