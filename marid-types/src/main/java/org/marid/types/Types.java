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

import java.lang.reflect.*;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.marid.types.GenericArrayTypes.genericArrayType;
import static org.marid.types.ParameterizedTypes.*;
import static org.marid.types.TypeUnification.resolve;
import static org.marid.types.TypeVariables.bounds;
import static org.marid.types.WildcardTypes.*;

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
      return Classes.arrayClass(toRaw(componentType, passed));
    } else if (type instanceof WildcardType) {
      return upperBounds((WildcardType) type)
          .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(passed, t))
          .map(t -> toRaw(t, passed))
          .min(Types::compareCovariantly)
          .orElse(Object.class);
    } else if (type instanceof TypeVariable<?>) {
      final var newPassed = TypeUtils.add(passed, type);
      return bounds((TypeVariable<?>) type)
          .filter(t -> !(t instanceof TypeVariable<?>) || !TypeUtils.contains(newPassed, t))
          .map(t -> toRaw(t, newPassed))
          .min(Types::compareCovariantly)
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
        if (m == null) {
          return type;
        } else {
          return substitute(m, mapping, newTypes);
        }
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

  static boolean isAssignableFrom(@NotNull Type target, @NotNull Type source, boolean covariance) {
    if (target instanceof Class<?> && source instanceof Class<?>) {
      return Classes.isAssignableFrom((Class<?>) target, (Class<?>) source);
    } else {
      return isAssignableFrom(target, source, EMPTY_TYPES, EMPTY_TYPES, covariance);
    }
  }

  private static boolean isAssignableFrom(Type target, Type source, Type[] tp, Type[] sp, boolean covariance) {
    if (target.equals(source)) {
      return true;
    } else if (target instanceof Class<?>) {
      return isAssignableFrom((Class<?>) target, source, tp, sp, covariance);
    } else if (target instanceof TypeVariable<?>) {
      final var ntp = TypeUtils.add(tp, target);
      return ntp.length == tp.length
          || bounds((TypeVariable<?>) target).allMatch(b -> isAssignableFrom(b, source, ntp, sp, covariance));
    } else if (target instanceof WildcardType) {
      return upperBounds((WildcardType) target).allMatch(b -> isAssignableFrom(b, source, tp, sp, covariance))
          && lowerBounds((WildcardType) target).allMatch(b -> isAssignableFrom(source, b, tp, sp, covariance));
    } else if (target instanceof GenericArrayType) {
      return isAssignableFrom((GenericArrayType) target, source, tp, sp, covariance);
    } else if (target instanceof ParameterizedType) {
      return isAssignableFrom((ParameterizedType) target, source, tp, sp, covariance);
    } else {
      throw new IllegalArgumentException("Illegal target: " + target);
    }
  }

  private static boolean isAssignableFrom(Class<?> t, Type source, Type[] tp, Type[] sp, boolean covariance) {
    if (source instanceof Class<?>) {
      return t.isAssignableFrom((Class<?>) source);
    } else if (source instanceof ParameterizedType) {
      return t.isAssignableFrom((Class<?>) ((ParameterizedType) source).getRawType());
    } else if (source instanceof GenericArrayType) {
      final var s = (GenericArrayType) source;
      return t.isArray()
          ? isAssignableFrom(t.getComponentType(), s.getGenericComponentType(), tp, sp, covariance)
          : t.isAssignableFrom(Object[].class);
    } else if (source instanceof WildcardType) {
      return upperBounds((WildcardType) source).anyMatch(b -> isAssignableFrom(t, b, tp, sp, covariance))
          && lowerBounds((WildcardType) source).allMatch(b -> isAssignableFrom(b, t, tp, sp, covariance));
    } else if (source instanceof TypeVariable<?>) {
      final var s = (TypeVariable<?>) source;
      final var nsp = TypeUtils.add(sp, source);
      return nsp.length != sp.length && bounds(s).anyMatch(b -> isAssignableFrom(t, b, tp, nsp, covariance));
    } else {
      throw new IllegalArgumentException("Illegal source: " + source);
    }
  }

  private static boolean isAssignableFrom(GenericArrayType t, Type source, Type[] tp, Type[] sp, boolean covariance) {
    if (source instanceof Class<?>) {
      final var s = (Class<?>) source;
      return s.isArray() && isAssignableFrom(t.getGenericComponentType(), s.getComponentType(), tp, sp, covariance);
    } else if (source instanceof GenericArrayType) {
      final var s = (GenericArrayType) source;
      return isAssignableFrom(t.getGenericComponentType(), s.getGenericComponentType(), tp, sp, covariance);
    } else if (source instanceof WildcardType) {
      return upperBounds((WildcardType) source).anyMatch(b -> isAssignableFrom(t, b, tp, sp, covariance))
          && lowerBounds((WildcardType) source).allMatch(b -> isAssignableFrom(b, t, tp, sp, covariance));
    } else if (source instanceof TypeVariable<?>) {
      final var s = (TypeVariable<?>) source;
      final var nsp = TypeUtils.add(sp, source);
      return nsp.length != sp.length && bounds(s).anyMatch(b -> isAssignableFrom(t, b, tp, nsp, covariance));
    } else {
      return false;
    }
  }

  private static boolean isAssignableFrom(ParameterizedType target, Type source, Type[] tp, Type[] sp, boolean covariance) {
    if (source instanceof Class<?>) {
      final var s = (Class<?>) source;
      final var tRaw = (Class<?>) target.getRawType();
      if (!tRaw.isAssignableFrom(s)) {
        return false;
      } else {
        return isAssignable(tRaw.getTypeParameters(), resolve(target), resolve(s), tp, sp, covariance);
      }
    } else if (source instanceof ParameterizedType) {
      final var s = (ParameterizedType) source;
      final var sRaw = (Class<?>) s.getRawType();
      final var tRaw = (Class<?>) target.getRawType();
      if (!tRaw.isAssignableFrom(sRaw)) {
        return false;
      } else {
        return isAssignable(tRaw.getTypeParameters(), resolve(target), resolve(s), tp, sp, covariance);
      }
    } else if (source instanceof WildcardType) {
      return upperBounds((WildcardType) source).anyMatch(b -> isAssignableFrom(target, b, tp, sp, covariance));
    } else if (source instanceof TypeVariable<?>) {
      final var nsp = TypeUtils.add(sp, source);
      return nsp.length != sp.length
          && bounds((TypeVariable<?>) source).anyMatch(b -> isAssignableFrom(target, b, tp, nsp, covariance));
    } else {
      return false;
    }
  }

  private static boolean isAssignable(TypeVariable<?>[] tVars,
                                      Map<TypeVariable<?>, Type> tMap,
                                      Map<TypeVariable<?>, Type> sMap,
                                      Type[] tp,
                                      Type[] sp,
                                      boolean covariance) {
    for (final var tVar : tVars) {
      final var sResolvedVar = sMap.get(tVar);
      if (sResolvedVar == null) {
        return false;
      }
      final var tResolvedVar = tMap.get(tVar);
      if (tResolvedVar == null) {
        return false;
      }
      if (covariance) {
        return isAssignableFrom(tResolvedVar, sResolvedVar, tp, sp, covariance);
      } else {
        return tResolvedVar.equals(sResolvedVar);
      }
    }
    return true;
  }

  static int compare(boolean covariant, @NotNull Type t1, @NotNull Type t2) {
    if (t1.equals(t2)) {
      return 0;
    } else if (isAssignableFrom(t1, t2, covariant)) {
      return 1;
    } else if (isAssignableFrom(t2, t1, covariant)) {
      return -1;
    } else {
      return 0;
    }
  }

  static int compareInvariantly(@NotNull Type t1, @NotNull Type t2) {
    return compare(false, t1, t2);
  }

  static int compareCovariantly(@NotNull Type t1, @NotNull Type t2) {
    return compare(true, t1, t2);
  }
}
