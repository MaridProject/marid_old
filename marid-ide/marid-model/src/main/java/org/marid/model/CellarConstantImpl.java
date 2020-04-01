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

public final class CellarConstantImpl extends AbstractEntity implements CellarConstant {

  private String factory;
  private String selector;
  private String name;
  private final ArrayList<AbstractConstantArgument> arguments = new ArrayList<>();

  public CellarConstantImpl() {}

  public CellarConstantImpl(String factory, String selector, String name) {
    this.factory = factory;
    this.selector = selector;
    this.name = name;
  }

  @Override public String getFactory() { return factory; }
  @Override public void setFactory(String factory) { this.factory = factory; }
  @Override public String getSelector() { return selector; }
  @Override public void setSelector(String selector) { this.selector = selector; }
  @Override public String getName() { return name; }
  @Override public void setName(String name) { this.name = name; }
  @Override public ArrayList<AbstractConstantArgument> getArguments() { return arguments; }
  @Override public void addArgument(ConstantArgument argument) { arguments.add((AbstractConstantArgument) argument); }
}
