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

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

public interface ClassStreams {

  @NotNull
  static Stream<@NotNull Class<?>> superclasses(@NotNull Class<?> type) {
    if (type.isInterface()) {
      return Stream.empty();
    } else {
      return Stream.of(type).flatMap(ClassStreams::superclasses0);
    }
  }

  private static Stream<Class<?>> superclasses0(Class<?> type) {
    return Stream.concat(
        Stream.of(type),
        Stream.ofNullable(type.getSuperclass()).flatMap(ClassStreams::superclasses0)
    );
  }

  @NotNull
  static Stream<@NotNull Class<?>> interfaces(@NotNull Class<?> type) {
    return (type.isInterface()
        ? Stream.of(type).flatMap(ClassStreams::interfaces0)
        : superclasses(type).flatMap(t -> Arrays.stream(t.getInterfaces())).flatMap(ClassStreams::interfaces0)
    ).distinct()
        .sorted((i1, i2) -> {
          if (i1.equals(i2)) {
            return 0;
          } else if (i1.isAssignableFrom(i2)) {
            return 1;
          } else if (i2.isAssignableFrom(i1)) {
            return -1;
          } else {
            return 0;
          }
        });
  }

  private static Stream<Class<?>> interfaces0(Class<?> itf) {
    return Stream.concat(
        Stream.of(itf),
        Arrays.stream(itf.getInterfaces()).flatMap(ClassStreams::interfaces0)
    );
  }

  @NotNull
  static Stream<@NotNull Method> methods(@NotNull Class<?> type) {
    return Arrays.stream(type.getMethods());
  }

  @NotNull
  static Stream<@NotNull Field> fields(@NotNull Class<?> type) {
    return Arrays.stream(type.getFields());
  }

  @NotNull
  static Stream<@NotNull Constructor<?>> constructors(@NotNull Class<?> type) {
    return Arrays.stream(type.getConstructors());
  }

  @NotNull
  static Stream<@NotNull Method> publicMethods(@NotNull Class<?> type) {
    return methods(type).filter(m -> Classes.isPublic(m.getDeclaringClass()));
  }

  @NotNull
  static Stream<@NotNull Field> publicFields(@NotNull Class<?> type) {
    return fields(type).filter(f -> Classes.isPublic(f.getDeclaringClass()));
  }

  @NotNull
  static Stream<@NotNull Constructor<?>> publicConstructors(@NotNull Class<?> type) {
    return constructors(type).filter(c -> Classes.isPublic(c.getDeclaringClass()));
  }
}
