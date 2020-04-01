package org.marid.model;

/*-
 * #%L
 * marid-model
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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

public final class CellarImpl extends AbstractEntity implements Cellar {

  private String name;

  private final ArrayList<RackImpl> racks = new ArrayList<>();
  private final ArrayList<CellarConstantImpl> constants = new ArrayList<>();

  public CellarImpl() {}

  public CellarImpl(String name) {
    this.name = name;
  }

  @Override public String getName() { return name; }
  @Override public void setName(String name) { this.name = name; }
  @Override public void addConstant(CellarConstant constant) { constants.add((CellarConstantImpl) constant); }
  @Override public void addRack(Rack rack) { racks.add((RackImpl) rack); }
  @Override public ArrayList<CellarConstantImpl> getConstants() { return constants; }
  @Override public ArrayList<RackImpl> getRacks() { return racks; }
}
