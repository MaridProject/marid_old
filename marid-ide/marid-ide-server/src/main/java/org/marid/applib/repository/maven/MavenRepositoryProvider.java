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
package org.marid.applib.repository.maven;

import org.marid.applib.repository.RepositoryProvider;

import java.util.Map;
import java.util.Properties;

public class MavenRepositoryProvider implements RepositoryProvider {

  private final Properties properties = new Properties();

  public MavenRepositoryProvider() {
    properties.setProperty("searchUrl", "http://search.maven.org/solrsearch/select");
  }

  @Override
  public MavenRepository getRepository(String name, Map<String, String> properties) {
    final Properties props = new Properties(this.properties);
    properties.forEach(props::setProperty);
    return new MavenRepository(name, props);
  }

  @Override
  public Properties getProperties() {
    return properties;
  }

  @Override
  public String getName() {
    return "Maven";
  }

  @Override
  public String getDescription() {
    return "Maven Central Repository";
  }
}
