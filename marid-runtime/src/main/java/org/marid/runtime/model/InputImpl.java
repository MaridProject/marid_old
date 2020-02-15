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

public final class InputImpl extends AbstractEntity implements Input {

  private String name;
  private AbstractArgument argument;

  InputImpl() {}

  public InputImpl(String name, AbstractArgument argument) {
    this.name = name;
    this.argument = argument;
  }

  @Override public String getName() { return name; }
  @Override public void setName(String name) { this.name = name; }
  @Override public AbstractArgument getArgument() { return argument; }
  @Override public void setArgument(Argument argument) { this.argument = (AbstractArgument) argument; }
}
