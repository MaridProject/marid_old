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
