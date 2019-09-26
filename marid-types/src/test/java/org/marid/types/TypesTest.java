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

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.marid.types.ParameterizedTypes.parameterizedTypeFromClass;

@Tag("normal")
class TypesTest extends TypeSugar {

  private static Stream<Arguments> toRawData() {
    return Stream.of(
        arguments(String.class, String.class),
        arguments(p(List.class, Integer.class), List.class),
        arguments(v(C1.class, 0), List.class),
        arguments(v(C2.class, 1), Map.class),
        arguments(v(C3.class, 0), Map.class),
        arguments(p(C4.class.getGenericSuperclass(), 0), List[].class),
        arguments(v(C5.class, 0), Map.class),
        arguments(v(C6.class, 0), Object.class),
        arguments(p(Enum.class, 0), Enum.class)
    );
  }

  @ParameterizedTest
  @MethodSource("toRawData")
  void toRaw(Type from, Class<?> rawExpected) {
    final var rawActual = Types.toRaw(from);
    assertEquals(rawExpected, rawActual);
  }

  private static Stream<Arguments> groundData() {
    return Stream.of(
        arguments(parameterizedTypeFromClass(Enum.class), p(Enum.class, wu())),
        arguments(parameterizedTypeFromClass(C7.class), p(C7.class, wu(p(ArrayList.class, wu()))))
    );
  }

  @ParameterizedTest
  @MethodSource("groundData")
  void ground(Type from, Type expected) {
    final var actual = Types.ground(from);
    assertEquals(expected, actual);
  }

  private static Stream<Arguments> isAssignableFromData() {
    return Stream.of(
        arguments(int.class, long.class, false),
        arguments(long.class, int.class, true),
        arguments(long.class, Long.class, true),
        arguments(Long.class, long.class, true),
        arguments(a(Long.class), Long[].class, true),
        arguments(long[].class, long[].class, true),
        arguments(long[].class, int[].class, false),
        arguments(Object.class, int[].class, true),
        arguments(Object.class, int.class, true),
        arguments(Object.class, p(ArrayList.class, Integer.class), true),
        arguments(ArrayList.class, p(ArrayList.class, Integer.class), true),
        arguments(p(AbstractList.class, Integer.class), p(ArrayList.class, Integer.class), true),
        arguments(p(List.class, Long.class), p(ArrayList.class, Long.class), true),
        arguments(p(List.class, Integer.class), p(ArrayList.class, Long.class), false),
        arguments(p(List.class, Number.class), p(ArrayList.class, Integer.class), false),
        arguments(p(C7.class, EC7.class), C8.class, true),
        arguments(C8.class, p(C7.class, EC7.class), false),
        arguments(p(Pair.class, Integer.class, Long.class), p(Pair.class, Integer.class, Double.class), false)
    );
  }

  @ParameterizedTest
  @MethodSource("isAssignableFromData")
  void isAssignableFrom(Type target, Type source, boolean expected) {
    assertEquals(expected, Types.isAssignableFrom(target, source));
  }

  static class C1<E extends List<E> & Serializable> {}
  static class C2<M extends Map<Integer, List<Long>>, E extends M> {}
  static class C3<M extends Map<? extends M, Integer>> {}
  static class C4<E extends List<E>> extends ArrayList<E[]> {}
  static class C5<K extends E, E extends Map<K, Object>> {}
  static class C6<V> {}
  static class C7<E extends ArrayList<C7<E>>> {}

  static class EC7 extends ArrayList<C7<EC7>> {}

  static class C8 extends C7<EC7> {}
}
