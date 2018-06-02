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
package org.marid.ui.webide.base.model;

import org.marid.applib.repository.Repository;
import org.marid.applib.repository.RepositoryProvider;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.marid.function.Suppliers.reduce;

public class RepositoryItem {

  private String selector;
  private String name;
  private final ArrayList<RepositoryProperty> properties = new ArrayList<>();

  public RepositoryItem() {
  }

  public RepositoryItem(String selector, String name) {
    this.selector = selector;
    this.name = name;
  }

  public String getSelector() {
    return selector;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<RepositoryProperty> getProperties() {
    return properties;
  }

  public Repository repository(RepositoryProvider provider) {
    return provider.getRepository(
        name,
        reduce(properties.stream(), new TreeMap<>(), (a, e) -> a.put(e.getKey(), e.getValue()))
    );
  }
}
