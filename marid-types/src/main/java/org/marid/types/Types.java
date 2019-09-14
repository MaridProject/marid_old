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
import java.util.function.Function;
import java.util.stream.Stream;

import static org.marid.types.GenericArrayTypes.genericArrayType;
import static org.marid.types.ParameterizedTypes.owner;
import static org.marid.types.ParameterizedTypes.parameterizedTypeWithOwner;
import static org.marid.types.ParameterizedTypes.parameters;
import static org.marid.types.TypeVariables.bounds;
import static org.marid.types.WildcardTypes.lowerBounds;
import static org.marid.types.WildcardTypes.upperBounds;
import static org.marid.types.WildcardTypes.wildcardType;
import static org.marid.types.WildcardTypes.wildcardTypeUpperBounds;

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
      return upperBounds((WildcardType) type)
          .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(passed, t))
          .map(t -> toRaw(t, passed))
          .filter(Classes::notObject)
          .findFirst()
          .orElse(Object.class);
    } else if (type instanceof TypeVariable<?>) {
      final var newPassed = TypeUtils.add(passed, type);
      return bounds((TypeVariable<?>) type)
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
      return parameters((ParameterizedType) type).allMatch(Types::isGround);
    } else if (type instanceof GenericArrayType) {
      return isGround(((GenericArrayType) type).getGenericComponentType());
    } else if (type instanceof WildcardType) {
      return lowerBounds(((WildcardType) type)).allMatch(Types::isGround)
          && upperBounds(((WildcardType) type)).allMatch(Types::isGround);
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

  private static Type[] ground(Stream<Type> types, Type[] passed, Function<WildcardType, Stream<Type>> bounds) {
    return types
        .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(passed, t))
        .map(t -> ground(t, passed))
        .flatMap(t -> t instanceof WildcardType ? bounds.apply((WildcardType) t) : Stream.of(t))
        .distinct()
        .toArray(Type[]::new);
  }

  private static Type ground(Type type, Type[] passed) {
    if (isGround(type)) {
      return type;
    } else if (type instanceof TypeVariable<?>) {
      final var newPassed = TypeUtils.add(passed, type);
      return wildcardTypeUpperBounds(ground(bounds((TypeVariable<?>) type), newPassed, WildcardTypes::upperBounds));
    } else if (type instanceof WildcardType) {
      return wildcardType(
          ground(upperBounds((WildcardType) type), passed, WildcardTypes::upperBounds),
          ground(lowerBounds((WildcardType) type), passed, WildcardTypes::lowerBounds)
      );
    } else if (type instanceof ParameterizedType) {
      if (TypeUtils.contains(passed, type)) {
        return wildcardTypeUpperBounds(EMPTY_TYPES);
      } else {
        final var newPassed = TypeUtils.add(passed, type);
        final var parameterizedType = (ParameterizedType) type;
        return parameterizedTypeWithOwner(
            toRaw(type),
            owner(parameterizedType).map(t -> ground(t, newPassed)).orElse(null),
            parameters(parameterizedType).map(t -> ground(t, newPassed)).toArray(Type[]::new)
        );
      }
    } else if (type instanceof GenericArrayType) {
      return genericArrayType(ground(type, passed));
    } else {
      throw new IllegalArgumentException(type.getTypeName());
    }
  }

  static boolean isAssignableFrom(@NotNull Type target, @NotNull Type source) {
    if (target instanceof Class<?> && source instanceof Class<?>) {
      return Classes.isAssignableFrom((Class<?>) target, (Class<?>) source);
    } else {
      return isAssignableFrom(target, source, EMPTY_TYPES, EMPTY_TYPES);
    }
  }

  private static boolean isAssignableFrom(Type target, Type source, Type[] tp, Type[] sp) {
    if (target instanceof Class<?>) {
      return isAssignableFrom((Class<?>) target, source, tp, sp);
    } else if (target instanceof TypeVariable<?>) {
      final var ntp = TypeUtils.add(tp, target);
      return ntp.length == tp.length
          || bounds((TypeVariable<?>) target).allMatch(b -> isAssignableFrom(b, source, ntp, sp));
    } else if (target instanceof WildcardType) {
      return upperBounds((WildcardType) target).allMatch(b -> isAssignableFrom(b, source, tp, sp))
          && lowerBounds((WildcardType) target).allMatch(b -> isAssignableFrom(source, b, tp, sp));
    } else if (target instanceof GenericArrayType) {
      return isAssignableFrom((GenericArrayType) target, source, tp, sp);
    } else if (target instanceof ParameterizedType) {
       return isAssignableFrom((ParameterizedType) target, source, tp, sp);
    } else {
      throw new IllegalArgumentException("Illegal target: " + target);
    }
  }

  private static boolean isAssignableFrom(Class<?> t, Type source, Type[] tp, Type[] sp) {
    if (source instanceof Class<?>) {
      return t.isAssignableFrom((Class<?>) source);
    } else if (source instanceof ParameterizedType) {
      return t.isAssignableFrom((Class<?>) ((ParameterizedType) source).getRawType());
    } else if (source instanceof GenericArrayType) {
      final var s = (GenericArrayType) source;
      return t.isArray()
          ? isAssignableFrom(t.getComponentType(), s.getGenericComponentType(), tp, sp)
          : t.isAssignableFrom(Object[].class);
    } else if (source instanceof WildcardType) {
      return upperBounds((WildcardType) source).anyMatch(b -> isAssignableFrom(t, b, tp, sp))
          && lowerBounds((WildcardType) source).allMatch(b -> isAssignableFrom(b, t, tp, sp));
    } else if (source instanceof TypeVariable<?>) {
      final var nsp = TypeUtils.add(sp, source);
      return nsp.length != sp.length && bounds((TypeVariable<?>) source).anyMatch(b -> isAssignableFrom(t, b, tp, nsp));
    } else {
      throw new IllegalArgumentException("Illegal source: " + source);
    }
  }

  private static boolean isAssignableFrom(GenericArrayType t, Type source, Type[] tp, Type[] sp) {
    if (source instanceof Class<?>) {
      final var s = (Class<?>) source;
      return s.isArray() && isAssignableFrom(t.getGenericComponentType(), s.getComponentType(), tp, sp);
    } else if (source instanceof GenericArrayType) {
      final var s = (GenericArrayType) source;
      return isAssignableFrom(t.getGenericComponentType(), s.getGenericComponentType(), tp, sp);
    } else if (source instanceof WildcardType) {
      return upperBounds((WildcardType) source).anyMatch(b -> isAssignableFrom(t, b, tp, sp))
          && lowerBounds((WildcardType) source).allMatch(b -> isAssignableFrom(b, t, tp, sp));
    } else if (source instanceof TypeVariable<?>) {
      final var nsp = TypeUtils.add(sp, source);
      return nsp.length != sp.length && bounds((TypeVariable<?>) source).anyMatch(b -> isAssignableFrom(t, b, tp, nsp));
    } else {
      return false;
    }
  }

  private static boolean isAssignableFrom(ParameterizedType target, Type source, Type[] tp, Type[] sp) {
    if (source instanceof Class<?>) {

    }
    return false;
  }
}
