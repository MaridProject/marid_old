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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Optional;
import java.util.stream.Stream;

public interface Types {

  Type[] EMPTY_TYPES = {};

  @NotNull
  static Class<?> toRaw(@NotNull Type type) {
    return toRaw(type, EMPTY_TYPES);
  }

  private static Class toRaw(Type type, Type[] passed) {
    if (type instanceof Class<?>) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
      return (Class<?>) ((ParameterizedType) type).getRawType();
    } else if (type instanceof GenericArrayType) {
      final var componentType = ((GenericArrayType) type).getGenericComponentType();
      return toRaw(componentType, passed).arrayType();
    } else if (type instanceof WildcardType) {
      return WildcardTypes.upperBounds((WildcardType) type)
          .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(passed, t))
          .map(t -> toRaw(t, passed))
          .filter(Classes::notObject)
          .findFirst()
          .orElse(Object.class);
    } else if (type instanceof TypeVariable<?>) {
      final var newPassed = TypeUtils.add(passed, type);
      return TypeVariables.bounds((TypeVariable<?>) type)
          .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(newPassed, t))
          .map(t -> toRaw(t, newPassed))
          .filter(Classes::notObject)
          .findFirst()
          .orElse(Object.class);
    } else {
      throw new IllegalArgumentException(type.getTypeName());
    }
  }

  static boolean isGround(@NotNull Type type) {
    if (type instanceof Class<?>) {
      return true;
    } else if (type instanceof ParameterizedType) {
      return ParameterizedTypes.parameters((ParameterizedType) type).allMatch(Types::isGround);
    } else if (type instanceof GenericArrayType) {
      return isGround(((GenericArrayType) type).getGenericComponentType());
    } else if (type instanceof WildcardType) {
      return WildcardTypes.lowerBounds(((WildcardType) type)).allMatch(Types::isGround)
          && WildcardTypes.upperBounds(((WildcardType) type)).allMatch(Types::isGround);
    } else if (type instanceof TypeVariable<?>) {
      return false;
    } else {
      throw new IllegalArgumentException(type.getTypeName());
    }
  }

  @NotNull
  static Type ground(@NotNull Type type) {
    return ground(type, EMPTY_TYPES);
  }

  private static Type ground(@NotNull Type type, Type[] passed) {
    if (isGround(type)) {
      return type;
    } else if (type instanceof TypeVariable<?>) {
      final var newPassed = TypeUtils.add(passed, type);
      return WildcardTypes.wildcardTypeUpperBounds(TypeVariables.bounds((TypeVariable<?>) type)
          .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(newPassed, t))
          .map(t -> ground(t, newPassed))
          .flatMap(t -> t instanceof WildcardType ? WildcardTypes.upperBounds((WildcardType) t) : Stream.of(t))
          .toArray(Type[]::new)
      );
    } else if (type instanceof WildcardType) {
      return WildcardTypes.wildcardType(
          WildcardTypes.upperBounds((WildcardType) type)
              .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(passed, t))
              .map(t -> ground(t, passed))
              .toArray(Type[]::new),
          WildcardTypes.lowerBounds((WildcardType) type)
              .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(passed, t))
              .map(t -> ground(t, passed))
              .toArray(Type[]::new)
      );
    } else if (type instanceof ParameterizedType) {
      if (TypeUtils.contains(passed, type)) {
        return WildcardTypes.wildcardTypeUpperBounds(EMPTY_TYPES);
      } else {
        final var newPassed = TypeUtils.add(passed, type);
        return ParameterizedTypes.parameterizedTypeWithOwner(
            (Class<?>) ((ParameterizedType) type).getRawType(),
            Optional.ofNullable(((ParameterizedType) type).getOwnerType())
                .map(t -> ground(t, newPassed))
                .orElse(null),
            ParameterizedTypes.parameters((ParameterizedType) type)
                .map(t -> ground(t, newPassed))
                .toArray(Type[]::new)
        );
      }
    } else if (type instanceof GenericArrayType) {
      return GenericArrayTypes.genericArrayType(ground(type, passed));
    } else {
      throw new IllegalArgumentException(type.getTypeName());
    }
  }
}
