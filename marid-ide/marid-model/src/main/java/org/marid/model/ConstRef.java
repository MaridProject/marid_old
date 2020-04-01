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

import org.w3c.dom.Element;

public interface ConstRef extends ConstantArgument {

  String getCellar();

  void setCellar(String cellar);

  String getRef();

  void setRef(String ref);

  @Override
  default String tag() {
    return "const-ref";
  }

  @Override
  default void readFrom(Element element) {
    setCellar(element.getAttribute("cellar"));
    setRef(element.getAttribute("ref"));
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("cellar", getCellar());
    element.setAttribute("ref", getRef());
  }
}
