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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marid.types.ClassStreams;

import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("normal")
class ClassStreamsTest {

  private static Stream<Arguments> superclassesData() {
    return Stream.of(
        arguments(C1.class, List.of(C1.class, C2.class, C3.class, C4.class, Object.class)),
        arguments(ArrayList.class, List.of(ArrayList.class, AbstractList.class, AbstractCollection.class, Object.class))
    );
  }

  @ParameterizedTest
  @MethodSource("superclassesData")
  void superclasses(Class<?> type, List<Class<?>> expected) {
    Assertions.assertEquals(expected, ClassStreams.superclasses(type).collect(Collectors.toList()));
  }

  private static Stream<Arguments> interfacesData() {
    return Stream.of(
        arguments(C1.class, List.of(I3.class, I1.class, I2.class))
    );
  }

  @ParameterizedTest
  @MethodSource("interfacesData")
  void interfaces(Class<?> type, List<Class<?>> expected) {
    assertEquals(expected, ClassStreams.interfaces(type).collect(Collectors.toList()));
  }

  @Test
  void publicMethods() {
    assertEquals(
        Set.of("m1", "m2", "m3", "im3", "im1"),
        ClassStreams.publicMethods(C1.class)
            .filter(m -> m.getDeclaringClass() != Object.class)
            .map(Method::getName)
            .collect(Collectors.toSet())
    );
  }

  public static class C1 extends C2 implements I1 {
    public void m1() {}
  }

  static class C2 extends C3 implements I3 {
    public void m2() {}
  }

  private static class C3 extends C4 implements I2 {
    public void m3() {}
  }

  public static class C4 implements I1 {
  }

  public interface I1 extends I2 {
    default void im1() {}
  }

  interface I2 {
    default void im2() {}
  }

  public interface I3 extends I2, I1 {
    default void im3() {}
  }
}
