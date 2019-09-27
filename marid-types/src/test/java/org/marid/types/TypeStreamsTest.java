package org.marid.types;

/*-
 * #%L
 * marid-types
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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marid.types.TypeUnificationTest.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("normal")
class TypeStreamsTest extends TypeSugar {

  private static Stream<Arguments> superclassesData() {
    return Stream.of(
        arguments(p(ArrayList.class, Integer.class), List.of(
            p(ArrayList.class, Integer.class),
            p(AbstractList.class, Integer.class),
            p(AbstractCollection.class, Integer.class),
            Object.class
        )),
        arguments(p(HashMap.class, Long.class, p(List.class, Long.class)), List.of(
            p(HashMap.class, Long.class, p(List.class, Long.class)),
            p(AbstractMap.class, Long.class, p(List.class, Long.class)),
            Object.class
        )),
        arguments(p(LinkedHashMap.class, Long.class, v(LinkedHashMap.class, 1)), List.of(
            p(LinkedHashMap.class, Long.class, v(LinkedHashMap.class, 1)),
            p(HashMap.class, Long.class, v(LinkedHashMap.class, 1)),
            p(AbstractMap.class, Long.class, v(LinkedHashMap.class, 1)),
            Object.class
        )),
        arguments(a(p(ArrayList.class, Integer.class)), List.of(
            a(p(ArrayList.class, Integer.class)),
            a(p(AbstractList.class, Integer.class)),
            a(p(AbstractCollection.class, Integer.class)),
            a(p(List.class, Integer.class)),
            a(p(Collection.class, Integer.class)),
            a(p(Iterable.class, Integer.class)),
            RandomAccess[].class,
            Cloneable[].class,
            Serializable[].class,
            Object[].class,
            Object.class
        )),
        arguments(p(CTC7.class, CTC3.class), List.of(
            p(CTC7.class, CTC3.class),
            p(CTC7.class, CTI1.class),
            p(CTC7.class, CTI2.class),
            p(CTC7.class, Object.class),
            p(CTC6.class, CTC3.class),
            p(CTC6.class, CTI1.class),
            p(CTC6.class, CTI2.class),
            p(CTC6.class, Object.class),
            Object.class
        ))
    );
  }

  @ParameterizedTest
  @MethodSource("superclassesData")
  void superclasses(Type type, List<Type> expected) {
    final var actual = TypeStreams.superclasses(type).collect(Collectors.toList());
    assertEquals(expected, actual);
  }

  private static Stream<Arguments> interfacesData() {
    return Stream.of(
        arguments(p(ArrayList.class, Integer.class), List.of(
            p(List.class, Integer.class),
            p(Collection.class, Integer.class),
            p(Iterable.class, Integer.class),
            RandomAccess.class,
            Cloneable.class,
            Serializable.class
        )),
        arguments(a(p(ArrayList.class, Integer.class)), List.of(
            Cloneable.class,
            Serializable.class
        )),
        arguments(p(CTC7.class, CTC3.class), List.of(
        ))
    );
  }

  @ParameterizedTest
  @MethodSource("interfacesData")
  void interfaces(Type type, List<Type> expected) {
    final var actual = TypeStreams.interfaces(type).collect(Collectors.toList());
    assertEquals(expected, actual);
  }
}
