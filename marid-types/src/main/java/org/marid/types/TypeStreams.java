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

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static org.marid.types.Types.*;

public interface TypeStreams {

  @NotNull
  static Stream<@NotNull Type> superclasses(@NotNull Type type) {
    if (type instanceof WildcardType || type instanceof TypeVariable<?>) {
      return superclasses(type, EMPTY_TYPES).distinct().sorted(Types::compare);
    } else {
      return superclasses(wrapIfPrimitive(type), EMPTY_TYPES);
    }
  }

  static Stream<Type> superclasses(Type type, Type[] passed) {
    if (type instanceof GenericArrayType) {
      final var t = (GenericArrayType) type;
      return Stream.concat(
          superclasses(t.getGenericComponentType(), passed).map(GenericArrayTypes::genericArray),
          Stream.concat(
              interfaces(t.getGenericComponentType(), passed).map(GenericArrayTypes::genericArray),
              Stream.of(Object.class)
          )
      ).sorted(Types::compare);
    } else if (type instanceof ParameterizedType) {
      final var t = (ParameterizedType) type;
      final var raw = (Class<?>) t.getRawType();
      if (raw.isInterface()) {
        return Stream.empty();
      } else {
        final var map = TypeUnification.resolve(t);
        return ClassStreams.superclasses(raw).flatMap(c -> expand(map, c));
      }
    } else if (type instanceof TypeVariable<?>) {
      final var newPassed = TypeUtils.add(passed, type);
      if (newPassed.length == passed.length) {
        return Stream.empty();
      } else {
        return TypeVariables.bounds((TypeVariable<?>) type).flatMap(t -> superclasses(t, newPassed));
      }
    } else if (type instanceof WildcardType) {
      return WildcardTypes.upperBounds((WildcardType) type).flatMap(t -> superclasses(t, passed));
    } else if (type instanceof Class<?>) {
      final var t = (Class<?>) type;
      if (t.isInterface()) {
        return Stream.empty();
      } else if (t.isArray() && !t.getComponentType().isPrimitive()) {
        return Stream.concat(
            superclasses(t.getComponentType(), passed).map(GenericArrayTypes::genericArray),
            Stream.concat(
                interfaces(t.getComponentType(), passed).map(GenericArrayTypes::genericArray),
                Stream.of(Object.class)
            )
        ).sorted(Types::compare);
      } else {
        final var map = TypeUnification.resolve(type);
        return ClassStreams.superclasses((Class<?>) type).flatMap(c -> expand(map, c));
      }
    } else {
      throw new IllegalArgumentException("Unsupported type: " + type);
    }
  }

  private static Stream<Type> expand(Map<TypeVariable<?>, Type> map, Class<?> c) {
    final var vars = c.getTypeParameters();
    if (vars.length == 0) {
      return Stream.of(c);
    } else {
      final var types = Arrays.stream(vars)
          .map(v -> {
            final var m = map.getOrDefault(v, v);
            if (m instanceof TypeVariable<?> || !VarianceProvider.checkCovariant(v)) {
              return new Type[] {m};
            } else {
              return Stream.concat(superclasses(m), interfaces(m)).sorted(Types::compare).toArray(Type[]::new);
            }
          })
          .toArray(Type[][]::new);
      return TypeUtils.combinations(types)
          .map(v -> ParameterizedTypes.parameterizedTypeWithOwner(c, c.getDeclaringClass(), v));
    }
  }

  @NotNull
  static Stream<@NotNull Type> interfaces(@NotNull Type type) {
    return interfaces(wrapIfPrimitive(type), EMPTY_TYPES).distinct().sorted(Types::compare);
  }

  private static Stream<Type> interfaces(Type type, Type[] passed) {
    if (type instanceof GenericArrayType) {
      return Arrays.stream(Object[].class.getGenericInterfaces());
    } else if (type instanceof ParameterizedType) {
      final var t = (ParameterizedType) type;
      final var raw = (Class<?>) t.getRawType();
      final var map = TypeUnification.resolve(t);
      return ClassStreams.interfaces(raw).flatMap(c -> expand(map, c));
    } else if (type instanceof TypeVariable<?>) {
      final var newPassed = TypeUtils.add(passed, type);
      if (newPassed.length == passed.length) {
        return Stream.empty();
      } else {
        return TypeVariables.bounds((TypeVariable<?>) type).flatMap(t -> interfaces(t, newPassed));
      }
    } else if (type instanceof WildcardType) {
      return WildcardTypes.upperBounds((WildcardType) type).flatMap(t -> interfaces(t, passed));
    } else if (type instanceof Class<?>) {
      final var map = TypeUnification.resolve(type);
      return ClassStreams.interfaces((Class<?>) type).flatMap(c -> expand(map, c));
    } else {
      throw new IllegalArgumentException("Unsupported type: " + type);
    }
  }

  @NotNull
  static Collector<@NotNull Type, @NotNull List<@NotNull Type>, @NotNull List<@NotNull Type>> superless() {
    final BiConsumer<List<Type>, Type> adder = (a, e) -> {
      if (a.stream().noneMatch(t -> isAssignableFrom(e, t))) {
        a.removeIf(t -> Types.isAssignableFrom(t, e));
        a.add(e);
      }
    };
    return Collector.of(ArrayList::new, adder, (a1, a2) -> {
      a1.forEach(e -> adder.accept(a2, e));
      return a2;
    });
  }
}
