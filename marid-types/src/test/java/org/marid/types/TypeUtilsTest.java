package org.marid.types;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("normal")
class TypeUtilsTest extends TypeSugar {

  private static final Type[] TYPES = {
      Object.class,
      p(ArrayList.class, Integer.class),
      String.class,
      wu(String.class),
      wl(Object.class),
      p(Map.class, Integer.class, int.class),
      a(p(List.class, w())),
      int.class,
      Integer.class,
      void.class,
      Void.class,
      a(a(p(List.class, Void.class)))
  };

  private static final Type[] ADDING = {
      Object.class,
      p(ArrayList.class, Integer.class),
      String.class,
      wu(Long.class),
      wl(Object.class),
      p(Map.class, Integer.class, int.class),
      a(p(List.class, w())),
      int.class,
      Object.class,
      String.class,
      Integer.class,
      void.class,
      Void.class,
      a(a(p(ArrayList.class, Void.class))),
      Double.class,
      Float.class,
      float.class
  };

  private static Stream<Arguments> addData() {
    return Stream
        .generate(() -> {
          final var types = TYPES.clone();
          Collections.shuffle(Arrays.asList(types));
          return types;
        })
        .limit(100)
        .map(types -> Arguments.arguments(types, ADDING[ThreadLocalRandom.current().nextInt(ADDING.length)]));
  }

  @ParameterizedTest
  @MethodSource("addData")
  void add(Type[] types, Type type) {
    final var expected = Stream.concat(Arrays.stream(types), Stream.of(type)).collect(Collectors.toUnmodifiableSet());
    final var actual = Set.of(TypeUtils.add(types, type));
    assertEquals(expected, actual);
  }

  @Test
  void testTypes() {
    final var type = (ParameterizedType) AbstractCollection.class.getGenericInterfaces()[0];
    final var e = (TypeVariable<?>) type.getActualTypeArguments()[0];
    System.out.println(e.getGenericDeclaration());
  }

  private static class X extends ArrayList<Integer> implements Collection<Integer> {
  }
}
