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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.marid.types.ClassStreamsTest.C1;
import org.marid.types.ClassStreamsTest.C4;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("normal")
class ClassesTest {

  @Test
  void isPublic() {
    assertEquals(
        List.of(C1.class, C4.class, Object.class),
        ClassStreams.superclasses(C1.class)
            .filter(Classes::isPublic)
            .collect(Collectors.toList())
    );
  }

  private static Stream<Arguments> isAssignableFromData() {
    return Stream.of(
        arguments(Object.class, Integer.class, true),
        arguments(Number.class, Integer.class, true),
        arguments(long.class, int.class, true),
        arguments(int.class, long.class, false),
        arguments(Number.class, Object.class, false),
        arguments(double.class, float.class, true),
        arguments(float.class, double.class, false),
        arguments(float.class, Double.class, false),
        arguments(double.class, Float.class, true),
        arguments(float.class, Float.class, true),
        arguments(Float.class, float.class, true)
    );
  }

  @ParameterizedTest
  @MethodSource("isAssignableFromData")
  void isAssignableFrom(Class<?> target, Class<?> source, boolean expected) {
    assertEquals(expected, Classes.isAssignableFrom(target, source));
  }
}
