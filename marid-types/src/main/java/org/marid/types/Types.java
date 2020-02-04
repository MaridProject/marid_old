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
import java.util.function.Function;
import java.util.stream.Stream;

import static org.marid.types.GenericArrayTypes.genericArrayType;
import static org.marid.types.ParameterizedTypes.owner;
import static org.marid.types.ParameterizedTypes.parameterizedTypeWithOwner;
import static org.marid.types.ParameterizedTypes.parameters;
import static org.marid.types.TypeResolution.resolveVars;
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

  @SuppressWarnings("rawtypes")
  private static Class toRaw(Type type, Type[] passed) {
    if (type instanceof Class<?>) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
      return (Class<?>) ((ParameterizedType) type).getRawType();
    } else if (type instanceof GenericArrayType) {
      final var componentType = ((GenericArrayType) type).getGenericComponentType();
      return Classes.arrayClass(toRaw(componentType, passed));
    } else if (type instanceof WildcardType) {
      return upperBounds((WildcardType) type)
        .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(passed, t))
        .map(t -> toRaw(t, passed))
        .min(Types::compare)
        .orElse(Object.class);
    } else if (type instanceof TypeVariable<?>) {
      final var newPassed = TypeUtils.add(passed, type);
      return bounds((TypeVariable<?>) type)
        .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(newPassed, t))
        .map(t -> toRaw(t, newPassed))
        .min(Types::compare)
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

  static Type substitute(@NotNull Type type, @NotNull Function<@NotNull TypeVariable<?>, @Nullable Type> mapping) {
    return substitute(type, mapping, EMPTY_TYPES);
  }

  private static Type substitute(Type type, Function<TypeVariable<?>, Type> mapping, Type[] passed) {
    if (isGround(type)) {
      return type;
    } else if (type instanceof GenericArrayType) {
      final var ct = ((GenericArrayType) type).getGenericComponentType();
      final var rt = substitute(ct, mapping);
      return rt.equals(ct) ? type : genericArrayType(substitute(ct, mapping));
    } else if (type instanceof ParameterizedType) {
      final var t = (ParameterizedType) type;
      final var args = t.getActualTypeArguments();
      final boolean changed = resolvedTypes(args, mapping, passed);
      return changed ? parameterizedTypeWithOwner((Class<?>) t.getRawType(), t.getOwnerType(), args) : type;
    } else if (type instanceof WildcardType) {
      final var t = (WildcardType) type;
      final var ubs = t.getUpperBounds();
      final var lbs = t.getLowerBounds();
      final var ubsChanged = resolvedTypes(ubs, mapping, passed);
      final var lbsChanged = resolvedTypes(lbs, mapping, passed);
      return ubsChanged || lbsChanged ? wildcardType(ubs, lbs) : type;
    } else if (type instanceof TypeVariable<?>) {
      final var newTypes = TypeUtils.add(passed, type);
      final var cycle = newTypes.length == passed.length;
      if (cycle) {
        return type;
      } else {
        final var m = mapping.apply((TypeVariable<?>) type);
        return m == null ? type : substitute(m, mapping, newTypes);
      }
    } else {
      throw new IllegalArgumentException("Unknown type: " + type);
    }
  }

  private static boolean resolvedTypes(Type[] array, Function<TypeVariable<?>, Type> mapping, Type[] passed) {
    boolean changed = false;
    for (int i = 0; i < array.length; i++) {
      final var ot = array[i];
      final var nt = substitute(ot, mapping, passed);
      if (!ot.equals(nt)) {
        changed = true;
        array[i] = nt;
      }
    }
    return changed;
  }

  static boolean isAssignableFrom(@NotNull Type target, @NotNull Type source) {
    if (target instanceof Class<?> && source instanceof Class<?>) {
      return Classes.isAssignableFrom((Class<?>) target, (Class<?>) source);
    } else {
      return isAssignableFrom(target, source, EMPTY_TYPES, EMPTY_TYPES);
    }
  }

  private static boolean isAssignableFrom(Type target, Type source, Type[] tp, Type[] sp) {
    if (target.equals(source)) {
      return true;
    } else if (target instanceof Class<?>) {
      return isAssignableFrom((Class<?>) target, source, tp, sp);
    } else if (target instanceof TypeVariable<?>) {
      final var ntp = TypeUtils.add(tp, target);
      return ntp.length == tp.length || bounds((TypeVariable<?>) target).allMatch(b -> isAssignableFrom(b, source, ntp, sp));
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
      return isAssignable(t, (WildcardType) source, tp, sp);
    } else if (source instanceof TypeVariable<?>) {
      return isAssignable(t, (TypeVariable<?>) source, tp, sp);
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
      return isAssignable(t, (WildcardType) source, tp, sp);
    } else if (source instanceof TypeVariable<?>) {
      return isAssignable(t, (TypeVariable<?>) source, tp, sp);
    } else {
      return false;
    }
  }

  private static boolean isAssignableFrom(ParameterizedType target, Type source, Type[] tp, Type[] sp) {
    final var tRaw = (Class<?>) target.getRawType();
    if (source instanceof Class<?>) {
      final var s = (Class<?>) source;
      return tRaw.isAssignableFrom(s) && isAssignable(tRaw, target, s, tp, sp);
    } else if (source instanceof ParameterizedType) {
      final var s = (ParameterizedType) source;
      final var sRaw = (Class<?>) s.getRawType();
      return tRaw.isAssignableFrom(sRaw) && isAssignable(tRaw, target, s, tp, sp);
    } else if (source instanceof WildcardType) {
      return isAssignable(target, (WildcardType) source, tp, sp);
    } else if (source instanceof TypeVariable<?>) {
      return isAssignable(target, (TypeVariable<?>) source, tp, sp);
    } else {
      return false;
    }
  }

  private static boolean isAssignable(Type target, WildcardType source, Type[] tp, Type[] sp) {
    return upperBounds(source).anyMatch(b -> isAssignableFrom(target, b, tp, sp))
      && lowerBounds(source).allMatch(b -> isAssignableFrom(b, target, tp, sp));
  }

  private static boolean isAssignable(Type target, TypeVariable<?> source, Type[] tp, Type[] sp) {
    final var nsp = TypeUtils.add(sp, source);
    return nsp.length != sp.length && bounds(source).anyMatch(b -> isAssignableFrom(target, b, tp, nsp));
  }

  private static boolean isAssignable(Class<?> raw, ParameterizedType t, Type s, Type[] tp, Type[] sp) {
    final var sMap = resolveVars(s);
    final var params = raw.getTypeParameters();
    final var args = t.getActualTypeArguments();
    assert args.length == params.length;
    for (int i = 0; i < params.length; i++) {
      final var tVar = params[i];
      final var sResolvedVar = sMap.get(tVar);
      if (sResolvedVar == null) {
        return false;
      }
      final var arg = args[i];
      if (VarianceProvider.checkCovariant(tVar)) {
        if (!isAssignableFrom(arg, sResolvedVar, tp, sp)) {
          return false;
        }
      } else {
        if (arg.equals(sResolvedVar)) {
          continue;
        }
        if (sResolvedVar instanceof WildcardType) {
          if (WildcardTypes.upperBounds((WildcardType) sResolvedVar).noneMatch(arg::equals)) {
            return false;
          }
        } else {
          return false;
        }
      }
    }
    return true;
  }

  static int compare(@NotNull Type t1, @NotNull Type t2) {
    if (t1.equals(t2)) {
      return 0;
    } else if (isAssignableFrom(t1, t2)) {
      return 1;
    } else if (isAssignableFrom(t2, t1)) {
      return -1;
    } else if (t1 instanceof GenericArrayType && t2 instanceof GenericArrayType) {
      return GenericArrayTypes.compare((GenericArrayType) t1, (GenericArrayType) t2);
    } else if (t1 instanceof ParameterizedType && t2 instanceof ParameterizedType) {
      return ParameterizedTypes.compare((ParameterizedType) t1, (ParameterizedType) t2);
    } else if (t1 instanceof WildcardType && t2 instanceof WildcardType) {
      return WildcardTypes.compare((WildcardType) t1, (WildcardType) t2);
    } else if (t1 instanceof TypeVariable<?> && t2 instanceof TypeVariable<?>) {
      return TypeVariables.compare((TypeVariable<?>) t1, (TypeVariable<?>) t2);
    } else if (t1 instanceof Class<?> && t2 instanceof Class<?>) {
      return Classes.compare((Class<?>) t1, (Class<?>) t2);
    } else {
      final int c = Integer.compare(order(t1), order(t2));
      return c != 0 ? c : t1.getTypeName().compareTo(t2.getTypeName());
    }
  }

  static int order(@NotNull Type type) {
    if (type instanceof GenericArrayType) {
      return 0;
    } else if (type instanceof ParameterizedType) {
      return 1;
    } else if (type instanceof WildcardType) {
      return 2;
    } else if (type instanceof TypeVariable<?>) {
      return 3;
    } else {
      return 4;
    }
  }

  @NotNull
  static Type wrapIfPrimitive(@NotNull Type type) {
    return type instanceof Class<?> ? Classes.wrapper((Class<?>) type) : type;
  }
}
