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

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;

/**
 * Utility methods for classes.
 */
public interface Classes {

  /**
   * Empty reusable array of classes
   */
  Class<?>[] EMPTY_CLASSES = {};

  /**
   * Checks whether the given type is public.
   * @param type A type.
   * @return True if the given class is accessible from the public lookup: {@link MethodHandles#publicLookup()}.
   */
  static boolean isPublic(@NotNull Class<?> type) {
    try {
      MethodHandles.publicLookup().accessClass(type);
      return true;
    } catch (IllegalAccessException | SecurityException e) {
      return false;
    }
  }

  /**
   * Constructs a new array instance based on the given component type.
   * @param componentType A component type.
   * @return Array class instance.
   */
  @NotNull
  static Class<?> arrayClass(@NotNull Class<?> componentType) {
    return Array.newInstance(componentType, 0).getClass();
  }

  /**
   * Checks whether the given type is a wrapper.
   * @param type A class.
   * @return {@code true} if the given type is a wrapper class, {@code false} otherwise.
   * @see jdk.dynalink.linker.support.TypeUtilities#isWrapperType(Class)
   */
  static boolean isWrapper(@NotNull Class<?> type) {
    return type == Integer.class
        || type == Long.class
        || type == Short.class
        || type == Byte.class
        || type == Character.class
        || type == Double.class
        || type == Float.class
        || type == Boolean.class
        || type == Void.class;
  }

  /**
   * Returns a wrapper type for a primitive type or the given type itself. The main difference from
   * the {@link jdk.dynalink.linker.support.TypeUtilities#getWrapperType(Class)} is that if the given
   * type represents a non-primitive class then this class will be returned instead of {@code null}.
   * @param type A class.
   * @return A wrapper class for any primitive type or the passed type itself.
   * @see jdk.dynalink.linker.support.TypeUtilities#getWrapperType(Class)
   */
  @NotNull
  static Class<?> wrapper(@NotNull Class<?> type) {
    if (type.isPrimitive()) {
      if (type == int.class) return Integer.class;
      else if (type == long.class) return Long.class;
      else if (type == short.class) return Short.class;
      else if (type == byte.class) return Byte.class;
      else if (type == char.class) return Character.class;
      else if (type == double.class) return Double.class;
      else if (type == float.class) return Float.class;
      else if (type == boolean.class) return Boolean.class;
      else if (type == void.class) return Void.class;
    }
    return type;
  }

  /**
   * Returns a primitive class if the given type is a wrapper class.
   * @param type A class.
   * @return A primitive class if the given class is a wrapper class, {@code null} otherwise.
   * @see jdk.dynalink.linker.support.TypeUtilities#getPrimitiveType(Class)
   */
  @Nullable
  static Class<?> primitive(@NotNull Class<?> type) {
    if (type == Integer.class) return int.class;
    else if (type == Long.class) return long.class;
    else if (type == Short.class) return short.class;
    else if (type == Byte.class) return byte.class;
    else if (type == Character.class) return char.class;
    else if (type == Double.class) return double.class;
    else if (type == Float.class) return float.class;
    else if (type == Boolean.class) return boolean.class;
    else if (type == Void.class) return void.class;
    else return null;
  }

  /**
   * Checks whether the {@code target} is assignable from {@code source}
   * @param target Target class.
   * @param source Source class.
   * @return {@code true} if the {@code target} is assignable from {@code source}; {@code false} otherwise.
   */
  static boolean isAssignableFrom(@NotNull Class<?> target, @NotNull Class<?> source) {
    if (target.isAssignableFrom(source)) {
      return true;
    } else if (target.isPrimitive()) {
      if (target == double.class) {
        return source == Double.class
            || source == float.class || source == Float.class
            || source == long.class || source == Long.class
            || source == int.class || source == Integer.class
            || source == short.class || source == Short.class
            || source == byte.class || source == Byte.class
            || source == char.class || source == Character.class;
      } else if (target == float.class) {
        return source == Float.class
            || source == int.class || source == Integer.class
            || source == long.class || source == Long.class
            || source == short.class || source == Short.class
            || source == byte.class || source == Byte.class
            || source == char.class || source == Character.class;
      } else if (target == int.class) {
        return source == Integer.class
            || source == short.class || source == Short.class
            || source == char.class || source == Character.class
            || source == byte.class || source == Byte.class;
      } else if (target == long.class) {
        return source == Long.class
            || source == int.class || source == Integer.class
            || source == short.class || source == Short.class
            || source == char.class || source == Character.class
            || source == byte.class || source == Byte.class;
      } else if (target == short.class) {
        return source == Short.class || source == byte.class || source == Byte.class;
      } else if (target == boolean.class) {
        return source == Boolean.class;
      } else if (target == char.class) {
        return source == Character.class;
      } else if (target == void.class) {
        return source == Void.class;
      } else if (target == byte.class) {
        return source == Byte.class;
      } else {
        return false;
      }
    } else if (source.isPrimitive()) {
      return target.isAssignableFrom(wrapper(source));
    } else {
      return primitive(target) == source;
    }
  }

  /**
   * Compares two classes:
   * <ul>
   *   <li>If {@code c1} equals to {@code c2} then {@code c1} is equal to {@code c2}</li>
   *   <li>If {@code c1} is assignable from {@code c2} then {@code c1} is greater than {@code c2}</li>
   *   <li>If {@code c2} is assignable from {@code c1} then {@code c1} is lesser than {@code c2}</li>
   *   <li>If the declaring class of {@code c1} is not {@code null} but the declaring class of {@code c2} is null then
   *   {@code c1} is greater than {@code c2}</li>
   *   <li>If the declaring class of {@code c2} is not {@code null} but the declaring class of {@code c1} is null then
   *   {@code c1} is lesser than {@code c2}</li>
   * </ul>
   * @param c1 The first class.
   * @param c2 The second class.
   * @return Compare result: -1, 0 or 1.
   */
  static int compare(@NotNull Class<?> c1, @NotNull Class<?> c2) {
    if (c1.equals(c2)) {
      return 0;
    } else if (c1.isAssignableFrom(c2)) {
      return 1;
    } else if (c2.isAssignableFrom(c1)) {
      return -1;
    } else {
      final var d1 = c1.getDeclaringClass();
      final var d2 = c2.getDeclaringClass();
      if (d1 != null) {
        if (d2 != null) {
          final int c = compare(d1, d2);
          if (c != 0) {
            return c;
          }
        } else {
          return 1;
        }
      } else if (d2 != null) {
        return -1;
      }
      if (c1.isInterface()) {
        if (!c2.isInterface()) {
          return 1;
        }
      } else if (c2.isInterface()) {
        return -1;
      }
      if (c1.isArray()) {
        if (c2.isArray()) {
          return compare(c1.getComponentType(), c2.getComponentType());
        } else {
          return -1;
        }
      } else {
        if (c2.isArray()) {
          return 1;
        }
      }
      final var t1 = c1.getTypeParameters();
      final var t2 = c2.getTypeParameters();
      final int c = -Integer.compare(t1.length, t2.length);
      if (c != 0) {
        return c;
      }
      return c1.getName().compareTo(c2.getName());
    }
  }
}
