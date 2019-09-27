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
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.marid.test.util.Maps.map;

@Tag("normal")
class TypeUnificationTest extends TypeSugar {

  private static class C1 extends HashMap<Integer, ArrayList<?>> {}
  private static class C2<A> {}
  private static class C3<P> extends C2<List<P>> {}
  private static class C4<E, P extends List<E>> extends C3<P> {}

  private static Stream<Arguments> resolveTypesData() {
    return Stream.of(
        arguments(C1.class, map(
            v(HashMap.class, "K"), Integer.class,
            v(HashMap.class, "V"), p(ArrayList.class, w()),
            v(AbstractMap.class, "K"), v(HashMap.class, "K"),
            v(AbstractMap.class, "V"), v(HashMap.class, "V"),
            v(Map.class, "K"), v(HashMap.class, "K"),
            v(Map.class, "V"), v(HashMap.class, "V")
        )),
        arguments(p(C3.class, Integer.class), map(
            v(C3.class, "P"), Integer.class,
            v(C2.class, "A"), p(List.class, v(C3.class, 0))
        )),
        arguments(p(C4.class, Integer.class, p(List.class, Integer.class)), map(
            v(C4.class, "E"), Integer.class,
            v(C4.class, "P"), p(List.class, Integer.class),
            v(C3.class, "P"), v(C4.class, "P"),
            v(C2.class, "A"), p(List.class, v(C3.class, 0))
        ))
    );
  }

  @ParameterizedTest
  @MethodSource("resolveTypesData")
  void resolveTypes(Type type, Map<Var, Type> expected) {
    final var map = new LinkedHashMap<TypeVariable<?>, Type>();
    TypeUnification.resolveTypes(type, map);
    final var actual = prettyMap(map);
    assertEquals(expected, actual);
  }

  private static Stream<Arguments> resolveData() {
    return Stream.of(
        arguments(C1.class, map(
            v(HashMap.class, "K"), Integer.class,
            v(HashMap.class, "V"), p(ArrayList.class, w()),
            v(AbstractMap.class, "K"), Integer.class,
            v(AbstractMap.class, "V"), p(ArrayList.class, w()),
            v(Map.class, "K"), Integer.class,
            v(Map.class, "V"), p(ArrayList.class, w())
        )),
        arguments(p(C3.class, Integer.class), map(
            v(C3.class, "P"), Integer.class,
            v(C2.class, "A"), p(List.class, Integer.class)
        )),
        arguments(p(C4.class, Integer.class, p(List.class, Integer.class)), map(
            v(C4.class, "E"), Integer.class,
            v(C4.class, "P"), p(List.class, Integer.class),
            v(C3.class, "P"), p(List.class, Integer.class),
            v(C2.class, "A"), p(List.class, p(List.class, Integer.class))
        ))
    );
  }

  @ParameterizedTest
  @MethodSource("resolveData")
  void resolve(Type type, Map<Var, Type> expected) {
    final var map = TypeUnification.resolve(type);
    final var actual = prettyMap(map);
    assertEquals(expected, actual);
  }

  private interface CTI1 {}
  private static class CTC1 implements CTI1 {}
  private static class CTC2 implements CTI1 {}
  private interface CTI2 {}
  private static class CTC3 implements CTI1, CTI2 {}
  private static class CTC4 implements CTI1 {}
  private static class CTC5 extends CTC4 implements CTI2 {}
  private static class CTC6<@Covariant E> {}

  private static Stream<Arguments> commonTypesData() {
    return Stream.of(
        arguments(
            List.of(String.class, CharSequence.class),
            List.of(CharSequence.class)
        ),
        arguments(
            List.of(CTC1.class, CTC2.class),
            List.of(CTI1.class)
        ),
        arguments(
            List.of(CTC3.class, CTC5.class),
            List.of(CTI1.class, CTI2.class)
        ),
        arguments(
            List.of(p(Pair.class, Integer.class, Long.class), p(Pair.class, Integer.class, Double.class)),
            List.of(Serializable.class)
        ),
        arguments(
            List.of(p(CTC6.class, CTC3.class), p(CTC6.class, CTC5.class)),
            List.of(p(CTC6.class, wu(CTI2.class, CTI1.class)))
        )
    );
  }

  @ParameterizedTest
  @MethodSource("commonTypesData")
  void commonTypes(List<Type> types, List<Type> expected) {
    final var actual = TypeUnification.commonTypes(types.toArray(Type[]::new));
    assertEquals(expected, actual);
  }
}
