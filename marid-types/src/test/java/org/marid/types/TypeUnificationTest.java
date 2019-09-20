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

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("normal")
class TypeUnificationTest extends TypeSugar {

  private static Stream<Arguments> resolveTypesData() {
    return Stream.of(
        arguments(C1.class, Map.of(
            v(HashMap.class, "K"), Integer.class,
            v(HashMap.class, "V"), p(ArrayList.class, w()),
            v(AbstractMap.class, "K"), v(HashMap.class, "K"),
            v(AbstractMap.class, "V"), v(HashMap.class, "V"),
            v(Map.class, "K"), v(HashMap.class, "K"),
            v(Map.class, "V"), v(HashMap.class, "V")
        )),
        arguments(p(C3.class, Integer.class), Map.of(

        ))
    );
  }

  @ParameterizedTest
  @MethodSource("resolveTypesData")
  void resolveTypes(Type type, Map<Var, Type> expected) {
    final var map = new HashMap<TypeVariable<?>, Type>();
    TypeUnification.resolveTypes(type, map);
    final var actual = prettyMap(map);
    assertEquals(expected, actual);
  }
}

class C1 extends HashMap<Integer, ArrayList<?>> {
}

class C2<A> {
}

class C3<P> extends C2<List<P>> {
}

