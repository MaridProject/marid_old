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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static org.marid.types.Classes.wrapper;

public interface ClassStreams {

  @NotNull
  static Stream<@NotNull Class<?>> superclasses(@NotNull Class<?> type) {
    if (type.isInterface()) {
      return Stream.empty();
    } else {
      return Stream.of(wrapper(type)).flatMap(ClassStreams::superclasses0);
    }
  }

  private static Stream<Class<?>> superclasses0(Class<?> type) {
    if (type.isArray() && !type.getComponentType().isPrimitive()) {
      return Stream.concat(
          superclasses0(type.getComponentType()).map(Classes::arrayClass),
          Stream.of(Object.class)
      );
    } else {
      return Stream.concat(
          Stream.of(type),
          Stream.ofNullable(type.getSuperclass()).flatMap(ClassStreams::superclasses0)
      );
    }
  }

  @NotNull
  static Stream<@NotNull Class<?>> interfaces(@NotNull Class<?> type) {
    final var stream = type.isInterface()
        ? Stream.of(type)
        : superclasses(wrapper(type)).flatMap(t -> Arrays.stream(t.getInterfaces()));
    return stream
        .flatMap(ClassStreams::interfaces0)
        .distinct()
        .sorted(Classes::compare);
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

  @NotNull
  static Collector<@NotNull Class<?>, @NotNull List<@NotNull Class<?>>, @NotNull List<@NotNull Class<?>>> superless() {
    final BiConsumer<List<Class<?>>, Class<?>> adder = (a, e) -> {
      if (a.stream().noneMatch(t -> Classes.isAssignableFrom(e, t))) {
        a.removeIf(t -> Classes.isAssignableFrom(t, e));
        a.add(e);
      }
    };
    return Collector.of(ArrayList::new, adder, (a1, a2) -> {
      a1.forEach(e -> adder.accept(a2, e));
      return a2;
    });
  }
}
