package org.marid.runtime.model;

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

public final class RackImpl extends AbstractEntity implements Rack {

  private String name;
  private String factory;

  private final ArrayList<AbstractArgument> arguments = new ArrayList<>();
  private final ArrayList<InitializerImpl> initializers = new ArrayList<>();

  RackImpl() {}

  public RackImpl(String name, String factory) {
    this.name = name;
    this.factory = factory;
  }

  @Override public String getName() { return name; }
  @Override public void setName(String name) { this.name = name; }
  @Override public String getFactory() { return factory; }
  @Override public void setFactory(String factory) { this.factory = factory; }
  @Override public ArrayList<AbstractArgument> getArguments() { return arguments; }
  @Override public ArrayList<InitializerImpl> getInitializers() { return initializers; }
  @Override public void addArgument(Argument argument) { arguments.add((AbstractArgument) argument); }
  @Override public void addInitializer(Initializer initializer) { initializers.add((InitializerImpl) initializer); }
}
