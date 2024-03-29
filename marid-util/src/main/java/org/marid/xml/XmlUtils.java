package org.marid.xml;

/*-
 * #%L
 * marid-util
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

import java.util.function.Consumer;

public interface XmlUtils {

  static <E extends Tagged & XmlWritable> void appendTo(E self, Element target) {
    final var child = target.getOwnerDocument().createElement(self.getTag());
    target.appendChild(child);
    self.writeTo(child);
  }

  @SafeVarargs
  static Element append(Element target, String tag, Consumer<Element>... configurers) {
    final var child = target.getOwnerDocument().createElement(tag);
    target.appendChild(child);
    for (final var configurer : configurers) {
      configurer.accept(child);
    }
    return child;
  }
}
