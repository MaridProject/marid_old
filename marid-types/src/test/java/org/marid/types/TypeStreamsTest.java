package org.marid.types;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
        ))
    );
  }

  @ParameterizedTest
  @MethodSource("superclassesData")
  void superclasses(Type type, List<Type> expected) {
    final var actual = TypeStreams.superclasses(type).collect(Collectors.toList());
    assertEquals(expected, actual);
  }
}
