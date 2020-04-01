package org.marid.model;

/*-
 * #%L
 * marid-runtime
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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

import java.util.ArrayList;

public final class WineryImpl extends AbstractEntity implements Winery {

  private String group;
  private String name;
  private String version;
  private final ArrayList<CellarImpl> cellars = new ArrayList<>();

  WineryImpl() {}

  public WineryImpl(String group, String name, String version) {
    this.group = group;
    this.name = name;
    this.version = version;
  }

  @Override public String getGroup() { return group; }
  @Override public void setGroup(String group) { this.group = group; }
  @Override public String getName() { return name; }
  @Override public void setName(String name) { this.name = name; }
  @Override public String getVersion() { return version; }
  @Override public void setVersion(String version) { this.version = version; }
  @Override public ArrayList<CellarImpl> getCellars() { return cellars; }
  @Override public void addCellar(Cellar cellar) { cellars.add((CellarImpl) cellar); }
}
