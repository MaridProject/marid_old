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

public interface CellarConstant extends Entity {

  String getFactory();

  void setFactory(String factory);

  String getSelector();

  void setSelector(String selector);

  String getName();

  void setName(String name);

  List<? extends ConstantArgument> getArguments();

  void addArgument(ConstantArgument argument);

  @Override
  default String tag() {
    return "const";
  }

  @Override
  default void readFrom(Element element) {
    setFactory(element.getAttribute("factory"));
    setSelector(element.getAttribute("selector"));
    setName(element.getAttribute("name"));
    ModelObjectFactoryFriend.children(element).forEach(e -> {
      final var c = modelObjectFactory().newEntity(e.getTagName());
      c.readFrom(e);
      if (c instanceof ConstantArgument) {
        addArgument((ConstantArgument) c);
      }
    });
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("name", getName());
    element.setAttribute("factory", getFactory());
    element.setAttribute("selector", getSelector());
    for (final var argument : getArguments()) {
      final var e = element.getOwnerDocument().createElement(argument.tag());
      element.appendChild(e);
      argument.writeTo(e);
    }
  }
}
