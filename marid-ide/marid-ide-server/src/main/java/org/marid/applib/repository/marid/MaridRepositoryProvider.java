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
package org.marid.applib.repository.marid;

import org.marid.applib.repository.RepositoryProvider;

import java.util.Map;
import java.util.Properties;

public class MaridRepositoryProvider implements RepositoryProvider {

  @Override
  public MaridRepository getRepository(String name, Map<String, String> properties) {
    return new MaridRepository(name);
  }

  @Override
  public Properties getProperties() {
    return new Properties();
  }

  @Override
  public String getName() {
    return "Marid";
  }

  @Override
  public String getDescription() {
    return "Marid Proprietary Repository";
  }
}
