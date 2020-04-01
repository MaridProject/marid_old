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

public interface Winery extends Entity {

  String getGroup();

  void setGroup(String group);

  String getName();

  void setName(String name);

  String getVersion();

  void setVersion(String version);

  List<? extends Cellar> getCellars();

  void addCellar(Cellar cellar);

  @Override
  default String tag() {
    return "winery";
  }

  @Override
  default void readFrom(Element element) {
    setGroup(element.getAttribute("group"));
    setName(element.getAttribute("name"));
    setVersion(element.getAttribute("version"));
    ModelObjectFactoryFriend.children(element).forEach(e -> {
      final var c = modelObjectFactory().newEntity(e.getTagName());
      c.readFrom(e);
      if (c instanceof Cellar) {
        addCellar((Cellar) c);
      }
    });
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("group", getGroup());
    element.setAttribute("name", getName());
    element.setAttribute("version", getVersion());
    for (final var cellar : getCellars()) {
      final var e = element.getOwnerDocument().createElement(cellar.tag());
      element.appendChild(e);
      cellar.writeTo(e);
    }
  }
}
