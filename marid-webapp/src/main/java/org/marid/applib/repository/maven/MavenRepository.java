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
package org.marid.applib.repository.maven;

import org.marid.applib.repository.ArtifactFinder;
import org.marid.applib.repository.Repository;

import java.net.URI;
import java.util.Properties;

public class MavenRepository implements Repository {

  private final Properties properties;

  public MavenRepository(Properties properties) {
    this.properties = properties;
  }

  @Override
  public ArtifactFinder getArtifactFinder() {
    final String searchUrl = properties.getProperty("searchUrl");
    return new MavenArtifactFinder(URI.create(searchUrl));
  }
}
