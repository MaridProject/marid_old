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
import org.marid.types.TypeSugar;
import org.marid.types.Types;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
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
        arguments(
            String.class,
            String.class
        ),
        arguments(
            p(List.class, Integer.class),
            List.class
        ),
        arguments(
            v(C1.class, 0),
            List.class
        ),
        arguments(
            v(C2.class, 1),
            Map.class
        ),
        arguments(
            v(C3.class, 0),
            Map.class
        ),
        arguments(
            ((ParameterizedType) C4.class.getGenericSuperclass()).getActualTypeArguments()[0],
            List[].class
        ),
        arguments(
            v(C5.class, 0),
            Map.class
        ),
        arguments(
            v(C6.class, 0),
            Object.class
        )
    );
  }

  @ParameterizedTest
  @MethodSource("toRawData")
  void toRaw(Type from, Class<?> rawExpected) {
    final var rawActual = Types.toRaw(from);
    assertEquals(rawExpected, rawActual);
  }

  public static class C1<E extends List<E> & Serializable> {}
  public static class C2<M extends Map<Integer, List<Long>>, E extends M> {}
  public static class C3<M extends Map<? extends M, Integer>> {}
  public static class C4<E extends List<E>> extends ArrayList<E[]> {}
  public static class C5<K extends E, E extends Map<K, Object>> {}
  public static class C6<V> {}
}
