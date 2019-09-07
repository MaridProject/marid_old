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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

public interface Types {

  Type[] EMPTY_TYPES = {};

  @NotNull
  static Stream<@NotNull Type> upperBounds(@NotNull WildcardType type) {
    return Arrays.stream(type.getUpperBounds());
  }

  @NotNull
  static Stream<@NotNull Type> lowerBounds(@NotNull WildcardType type) {
    return Arrays.stream(type.getLowerBounds());
  }

  @NotNull
  static Stream<@NotNull Type> bounds(@NotNull TypeVariable<?> var) {
    return Arrays.stream(var.getBounds());
  }

  @NotNull
  static Class<?> toRaw(@NotNull Type type) {
    return toRaw(type, Collections.emptySet());
  }

  private static Class toRaw(Type type, Set<TypeVariable<?>> passed) {
    if (type instanceof Class<?>) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
      return (Class<?>) ((ParameterizedType) type).getRawType();
    } else if (type instanceof GenericArrayType) {
      final var componentType = ((GenericArrayType) type).getGenericComponentType();
      return toRaw(componentType, passed).arrayType();
    } else if (type instanceof WildcardType) {
      return upperBounds((WildcardType) type)
          .filter(t -> !(t instanceof TypeVariable<?>) || !passed.contains(t))
          .map(t -> toRaw(t, passed))
          .filter(Classes::notObject)
          .findFirst()
          .orElse(Object.class);
    } else if (type instanceof TypeVariable<?>) {
      final var newPassed = TypeUtils.add(passed, (TypeVariable<?>) type);
      return bounds((TypeVariable<?>) type)
          .filter(t -> !(t instanceof TypeVariable<?>) || !passed.contains(t))
          .map(t -> toRaw(t, newPassed))
          .filter(Classes::notObject)
          .findFirst()
          .orElse(Object.class);
    } else {
      throw new IllegalArgumentException(type.getTypeName());
    }
  }

  @NotNull
  static ParameterizedType parameterizedTypeFromClass(@NotNull Class<?> type) {
    return new ParameterizedTypeImpl(type, type.getTypeParameters(), type.getDeclaringClass());
  }

  @NotNull
  static Type parameterizedFromClass(@NotNull Class<?> type) {
    final var vars = type.getTypeParameters();
    return vars.length > 0 ? parameterizedTypeFromClass(type) : type;
  }

  @NotNull
  static ParameterizedType parameterizedType(@NotNull Class<?> raw, @NotNull Type... parameters) {
    return new ParameterizedTypeImpl(raw, parameters, raw.getDeclaringClass());
  }

  @NotNull
  static Type parameterized(@NotNull Class<?> raw, @NotNull Type... parameters) {
    if (parameters.length == 0 && raw.getTypeParameters().length == 0) {
      return raw;
    } else {
      return parameterizedType(raw, parameters);
    }
  }

  @NotNull
  static ParameterizedType parameterizedTypeWithOwner(@NotNull Class<?> raw, @Nullable Type owner, @NotNull Type... parameters) {
    return new ParameterizedTypeImpl(raw, parameters, owner);
  }

  @NotNull
  static Type parameterizedWithOwner(@NotNull Class<?> raw, @Nullable Type owner, @NotNull Type... parameters) {
    if (parameters.length == 0 && raw.getTypeParameters().length == 0 && owner == raw.getDeclaringClass()) {
      return raw;
    } else {
      return parameterizedTypeWithOwner(raw, owner, parameters);
    }
  }

  @NotNull
  static GenericArrayType genericArrayType(@NotNull Type component) {
    return new GenericArrayTypeImpl(component);
  }

  @NotNull
  static Type genericArray(@NotNull Type component) {
    return component instanceof Class<?> ? ((Class<?>) component).arrayType() : genericArrayType(component);
  }

  @NotNull
  static WildcardType wildcardTypeUpperBounds(@NotNull Type... upperBounds) {
    return new WildcardTypeImpl(upperBounds, EMPTY_TYPES);
  }

  @NotNull
  static WildcardType wildcardTypeLowerBounds(@NotNull Type... lowerBounds) {
    return new WildcardTypeImpl(EMPTY_TYPES, lowerBounds);
  }

  @NotNull
  static WildcardType wildcardType(@NotNull Type[] upperBounds, @NotNull Type[] lowerBounds) {
    return new WildcardTypeImpl(upperBounds, lowerBounds);
  }
}
