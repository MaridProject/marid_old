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
import org.marid.expression.generic.RefExpression;
import org.marid.expression.xml.XmlExpression;
import org.w3c.dom.Element;

import org.jetbrains.annotations.NotNull;

public class RefExpr extends Expr implements RefExpression {

  public final StringProperty ref;

  public RefExpr(@NotNull String ref) {
    this.ref = new SimpleStringProperty(ref);
  }

  public RefExpr(@NotNull Element element) {
    super(element);
    this.ref = new SimpleStringProperty(XmlExpression.ref(element));
  }

  @NotNull
  @Override
  public String getReference() {
    return ref.get();
  }

  @Override
  public void writeTo(@NotNull Element element) {
    super.writeTo(element);
    XmlExpression.ref(element, getReference());
  }

  @Override
  public String toString() {
    return "@(" + ref + ")";
  }
}