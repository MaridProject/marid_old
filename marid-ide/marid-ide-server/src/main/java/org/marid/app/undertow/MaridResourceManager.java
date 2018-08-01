/*-
 * #%L
 * marid-ide-server
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

import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Stream;

@Component
public class MaridResourceManager implements ResourceManager {

  private final ResourceManager[] resourceManagers;

  public MaridResourceManager(@Qualifier("resourceManager") ResourceManager[] resourceManagers) {
    this.resourceManagers = resourceManagers;
  }

  @Override
  public Resource getResource(String path) throws IOException {
    for (final var resourceManager : resourceManagers) {
      final var resource = resourceManager.getResource(path);
      if (resource != null) {
        return resource;
      }
    }
    return null;
  }

  @Override
  public boolean isResourceChangeListenerSupported() {
    return Stream.of(resourceManagers).anyMatch(ResourceManager::isResourceChangeListenerSupported);
  }

  @Override
  public void registerResourceChangeListener(ResourceChangeListener listener) {
    Stream.of(resourceManagers)
        .filter(ResourceManager::isResourceChangeListenerSupported)
        .forEach(m -> m.registerResourceChangeListener(listener));
  }

  @Override
  public void removeResourceChangeListener(ResourceChangeListener listener) {
    Stream.of(resourceManagers)
        .filter(ResourceManager::isResourceChangeListenerSupported)
        .forEach(m -> m.removeResourceChangeListener(listener));
  }

  @Override
  public void close() {
  }
}
