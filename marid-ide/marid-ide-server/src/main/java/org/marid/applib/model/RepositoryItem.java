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
package org.marid.applib.model;

import org.jetbrains.annotations.NotNull;
import org.marid.applib.repository.Repository;
import org.marid.applib.repository.RepositoryProvider;
import org.marid.misc.EHT;

import java.util.TreeMap;

public class RepositoryItem extends EHT implements Elem<String> {

  private transient final String id;

  private String selector;
  private TreeMap<String, String> properties = new TreeMap<>();

  public RepositoryItem(String id) {
    this.id = id;
  }

  public String getSelector() {
    return selector;
  }

  public RepositoryItem setSelector(String selector) {
    this.selector = selector;
    return this;
  }

  public TreeMap<String, String> getProperties() {
    return properties;
  }

  @NotNull
  @Override
  public String getId() {
    return id;
  }

  public Repository repository(RepositoryProvider provider) {
    return provider.getRepository(id, properties);
  }
}
