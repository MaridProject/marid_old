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
import java.util.Collections;

public final class InitializerImpl extends AbstractEntity implements Initializer {

  private String name;

  private final ArrayList<AbstractArgument> arguments = new ArrayList<>();

  public InitializerImpl() {}

  public InitializerImpl(String name, AbstractArgument... arguments) {
    this.name = name;
    Collections.addAll(this.arguments, arguments);
  }

  @Override public String getName() { return name; }
  @Override public void setName(String name) { this.name = name; }
  @Override public ArrayList<AbstractArgument> getArguments() { return arguments; }
  @Override public void addArgument(Argument argument) { this.arguments.add((AbstractArgument) argument); }
}
