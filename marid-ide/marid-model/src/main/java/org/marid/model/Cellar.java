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

import java.util.List;

public interface Cellar extends Entity {

  String getName();

  void setName(String name);

  List<? extends CellarConstant> getConstants();

  List<? extends Rack> getRacks();

  void addRack(Rack rack);

  void addConstant(CellarConstant constant);

  @Override
  default String tag() {
    return "cellar";
  }

  @Override
  default void readFrom(Element element) {
    setName(element.getAttribute("name"));
    ModelObjectFactoryFriend.children(element).forEach(e -> {
      final var c = modelObjectFactory().newEntity(e.getTagName());
      c.readFrom(e);
      if (c instanceof CellarConstant) {
        addConstant((CellarConstant) c);
      } else if (c instanceof Rack) {
        addRack((Rack) c);
      }
    });
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("name", getName());
    for (final var constant: getConstants()) {
      final var e = element.getOwnerDocument().createElement(constant.tag());
      element.appendChild(e);
      constant.writeTo(e);
    }
    for (final var rack: getRacks()) {
      final var e = element.getOwnerDocument().createElement(rack.tag());
      element.appendChild(e);
      rack.writeTo(e);
    }
  }
}
