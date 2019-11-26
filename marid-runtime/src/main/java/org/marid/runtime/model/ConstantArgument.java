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

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.function.Function;
import java.util.stream.Stream;

public abstract class ConstantArgument extends Argument {

  ConstantArgument() {
  }

  ConstantArgument(@NotNull Element element) {
    super(element);
  }

  private static Stream<Function<Element, ConstantArgument>> constructors() {
    return Stream.of(
        ArgumentConstRef::new,
        ArgumentLiteral::new
    );
  }

  static ConstantArgument argument(Element element) {
    return constructors()
        .flatMap(c -> {
          try {
            return Stream.of(c.apply(element));
          } catch (IllegalArgumentException e) {
            return Stream.empty();
          }
        })
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown argument: " + element.getTagName()));
  }

  static Argument argument(InputSource inputSource) {
    return argument(AbstractEntity.element(inputSource));
  }
}
