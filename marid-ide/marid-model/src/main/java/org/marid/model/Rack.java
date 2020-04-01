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

public interface Rack extends Entity {

  String getName();

  void setName(String name);

  String getFactory();

  void setFactory(String factory);

  List<? extends Argument> getArguments();

  List<? extends Initializer> getInitializers();

  void addArgument(Argument argument);

  void addInitializer(Initializer initializer);

  @Override
  default String tag() {
    return "rack";
  }

  @Override
  default void readFrom(Element element) {
    setName(element.getAttribute("name"));
    setFactory(element.getAttribute("factory"));
    ModelObjectFactoryFriend.children(element).forEach(e -> {
      final var c = modelObjectFactory().newEntity(e.getTagName());
      c.readFrom(e);
      if (c instanceof Argument) {
        addArgument((Argument) c);
      } else if (c instanceof Initializer) {
        addInitializer((Initializer) c);
      }
    });
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("name", getName());
    element.setAttribute("factory", getFactory());
    for (final var argument: getArguments()) {
      final var e = element.getOwnerDocument().createElement(argument.tag());
      element.appendChild(e);
      argument.writeTo(e);
    }
    for (final var initializer: getInitializers()) {
      final var e = element.getOwnerDocument().createElement(initializer.tag());
      element.appendChild(e);
      initializer.writeTo(e);
    }
  }
}
