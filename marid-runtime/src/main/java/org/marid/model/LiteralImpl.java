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

public final class LiteralImpl extends AbstractConstantArgument implements Literal {

  private Type type;
  private String value;

  LiteralImpl() {}

  public LiteralImpl(Type type, String value) {
    this.type = type;
    this.value = value;
  }

  @Override public Type getType() { return type; }
  @Override public void setType(Type type) { this.type = type; }
  @Override public String getValue() { return value; }
  @Override public void setValue(String value) { this.value = value; }
}
