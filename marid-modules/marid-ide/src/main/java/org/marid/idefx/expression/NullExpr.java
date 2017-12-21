/*-
 * #%L
 * marid-ide
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
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

package org.marid.idefx.expression;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.marid.expression.generic.NullExpression;
import org.marid.expression.xml.XmlExpression;
import org.w3c.dom.Element;

import org.jetbrains.annotations.NotNull;

public class NullExpr extends Expr implements NullExpression {

  public final StringProperty type;

  public NullExpr() {
    this(void.class.getName());
  }

  public NullExpr(@NotNull String type) {
    this.type = new SimpleStringProperty(type);
  }

  NullExpr(@NotNull Element element) {
    super(element);
    this.type = new SimpleStringProperty(XmlExpression.type(element));
  }

  @NotNull
  @Override
  public String getType() {
    return type.get();
  }

  @Override
  public void writeTo(@NotNull Element element) {
    super.writeTo(element);
    XmlExpression.type(element, getType());
  }

  @Override
  public String toString() {
    return "null(" + getType() + ")";
  }
}