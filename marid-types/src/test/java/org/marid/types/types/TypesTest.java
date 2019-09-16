package org.marid.types.types;

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
import org.marid.types.ParameterizedTypes;
import org.marid.types.TypeSugar;
import org.marid.types.Types;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
        arguments(
            ParameterizedTypes.parameterizedTypeFromClass(Enum.class),
            p(Enum.class, wu())
        ),
        arguments(
            ParameterizedTypes.parameterizedTypeFromClass(C7.class),
            p(C7.class, wu(p(ArrayList.class, wu())))
        )
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
        arguments(int.class, long.class, null, false),
        arguments(long.class, int.class, null, true),
        arguments(long.class, Long.class, null, true),
        arguments(Long.class, long.class, null, true),
        arguments(a(Long.class), Long[].class, null, true),
        arguments(long[].class, long[].class, null, true),
        arguments(long[].class, int[].class, null, false),
        arguments(Object.class, int[].class, null, true),
        arguments(Object.class, int.class, null, true)
    ).flatMap(args -> {
      final var v = args.get();
      if (v[2] == null) {
        return Stream.of(
            arguments(v[0], v[1], false, v[3]),
            arguments(v[0], v[1], true, v[3])
        );
      } else {
        return Stream.of(args);
      }
    });
  }

  @ParameterizedTest
  @MethodSource("isAssignableFromData")
  void isAssignableFrom(Type target, Type source, boolean covariance, boolean expected) {
    assertEquals(expected, Types.isAssignableFrom(target, source, covariance));
  }

  public static class C1<E extends List<E> & Serializable> {}
  public static class C2<M extends Map<Integer, List<Long>>, E extends M> {}
  public static class C3<M extends Map<? extends M, Integer>> {}
  public static class C4<E extends List<E>> extends ArrayList<E[]> {}
  public static class C5<K extends E, E extends Map<K, Object>> {}
  public static class C6<V> {}
  public static class C7<E extends ArrayList<C7<E>>> {}
}
