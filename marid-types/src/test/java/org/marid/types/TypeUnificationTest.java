package org.marid.types;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("normal")
class TypeUnificationTest extends TypeSugar {

  private static Stream<Arguments> resolveTypesData() {
    return Stream.of(
        arguments(C1.class, Map.of())
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

