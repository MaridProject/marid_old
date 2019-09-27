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

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public interface Classes {

  Class<?>[] EMPTY_CLASSES = {};

  static boolean isPublic(@NotNull Class<?> type) {
    try {
      MethodHandles.publicLookup().accessClass(type);
      return true;
    } catch (IllegalAccessException | SecurityException e) {
      return false;
    }
  }

  @NotNull
  static Class<?> arrayClass(@NotNull Class<?> componentType) {
    return Array.newInstance(componentType, 0).getClass();
  }

  static boolean notObject(@NotNull Class<?> type) {
    return type != Object.class;
  }

  static boolean hasAll(@NotNull Class<?> type, @MagicConstant(flagsFromClass = Modifier.class) int modifiers) {
    return (type.getModifiers() & modifiers) == modifiers;
  }

  static boolean hasAll(@NotNull Member member, @MagicConstant(flagsFromClass = Modifier.class) int modifiers) {
    return (member.getModifiers() & modifiers) == modifiers;
  }

  static boolean hasAny(@NotNull Class<?> type, @MagicConstant(flagsFromClass = Modifier.class) int modifiers) {
    return (type.getModifiers() & modifiers) != 0;
  }

  static boolean hasAny(@NotNull Member member, @MagicConstant(flagsFromClass = Modifier.class) int modifiers) {
    return (member.getModifiers() & modifiers) != 0;
  }

  static boolean hasNone(@NotNull Class<?> type, @MagicConstant(flagsFromClass = Modifier.class) int modifiers) {
    return (type.getModifiers() & modifiers) == 0;
  }

  static boolean hasNone(@NotNull Member member, @MagicConstant(flagsFromClass = Modifier.class) int modifiers) {
    return (member.getModifiers() & modifiers) == 0;
  }

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

  static int compare(@NotNull Class<?> c1, @NotNull Class<?> c2) {
    if (c1.equals(c2)) {
      return 0;
    } else if (c1.isAssignableFrom(c2)) {
      return 1;
    } else if (c2.isAssignableFrom(c1)) {
      return -1;
    } else {
      return 0;
    }
  }

  static int fullCompare(@NotNull Class<?> c1, @NotNull Class<?> c2) {
    final int c = compare(c1, c2);
    return c != 0 ? c : c1.getName().compareTo(c2.getName());
  }
}
