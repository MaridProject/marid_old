package org.marid.types;

/*-
 * #%L
 * marid-types
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

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.marid.test.util.Maps.map;

@Tag("normal")
class TypeResolutionTest extends TypeSugar {

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
    TypeResolution.resolveVars(type, map);
    final var actual = prettyMap(map);
    assertEquals(expected, actual);
  }

  private static Stream<Arguments> resolveVarsData() {
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
  @MethodSource("resolveVarsData")
  void resolveVars(Type type, Map<Var, Type> expected) {
    final var map = TypeResolution.resolveVars(type);
    final var actual = prettyMap(map);
    assertEquals(expected, actual);
  }

  interface CTI1 {}
  static class CTC1 implements CTI1 {}
  static class CTC2 implements CTI1 {}
  interface CTI2 {}
  static class CTC3 implements CTI1, CTI2 {}
  static class CTC4 implements CTI1 {}
  static class CTC5 extends CTC4 implements CTI2 {}
  static class CTC6<@Covariant E> {}
  static class CTC7<@Covariant E> extends CTC6<E> {}
  static class CTC8<@Covariant E> extends CTC6<E> {}

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
            List.of(p(CTC6.class, wu(CTI1.class, CTI2.class)))
        ),
        arguments(
            List.of(p(CTC6.class, CTC3.class), p(CTC6.class, CTC4.class)),
            List.of(p(CTC6.class, CTI1.class))
        ),
        arguments(
            List.of(p(CTC6.class, p(CTC7.class, String.class)), p(CTC7.class, p(CTC6.class, CharSequence.class))),
            List.of(p(CTC6.class, p(CTC6.class, CharSequence.class)))
        ),
        arguments(
            List.of(p(CTC7.class, CTC3.class), p(CTC8.class, CTC5.class)),
            List.of(p(CTC6.class, wu(CTI1.class, CTI2.class)))
        ),
        arguments(
            List.of(int.class, long.class),
            TypeResolution.commonTypes(List.of(Integer.class, Long.class)::stream)
        ),
        arguments(
            List.of(int.class, BigInteger.class),
            TypeResolution.commonTypes(List.of(Integer.class, BigInteger.class)::stream)
        ),
        arguments(
            List.of(BigInteger.class, BigDecimal.class),
            List.of(Number.class)
        ),
        arguments(
            List.of(p(CTC7.class, BigInteger.class), p(CTC8.class, BigDecimal.class)),
            List.of(p(CTC6.class, Number.class))
        )
    );
  }

  @ParameterizedTest
  @MethodSource("commonTypesData")
  void commonTypes(List<Type> types, List<Type> expected) {
    final var actual = TypeResolution.commonTypes(types::stream);
    assertEquals(expected, actual);
  }

  private static Stream<Arguments> resolveData() throws Exception {
    return Stream.of(
        arguments(String.class, String.class, List.of()),
        arguments(p(ArrayList.class), p(ArrayList.class, Integer.class), List.of(
            v(ArrayList.class, 0), Integer.class
        )),
        arguments(p(ArrayList.class), p(ArrayList.class, Integer.class), List.of(
            v(List.class, 0), Integer.class
        )),
        arguments(p(ArrayList.class), p(ArrayList.class, Integer.class), List.of(
            List.class.getMethod("addAll", Collection.class).getGenericParameterTypes()[0], p(List.class, Integer.class)
        ))
    );
  }

  @ParameterizedTest
  @MethodSource("resolveData")
  void resolve(Type source, Type expected, List<Type> types) {
    final var actual = TypeResolution.resolve(source, resolver -> {
      for (int i = 0; i < types.size() / 2; i++) {
        resolver.accept(types.get(2 * i), types.get(2 * i + 1));
      }
    });
    assertEquals(expected, actual);
  }
}
