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

import jdk.dynalink.linker.support.TypeUtilities;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
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

  static boolean hasNone(@NotNull Class<?> type, @MagicConstant (flagsFromClass = Modifier.class) int modifiers) {
    return (type.getModifiers() & modifiers) == 0;
  }

  static boolean hasNone(@NotNull Member member, @MagicConstant (flagsFromClass = Modifier.class) int modifiers) {
    return (member.getModifiers() & modifiers) == 0;
  }

  static boolean isAssignableFrom(@NotNull Class<?> target, @NotNull Class<?> source) {
    if (target.isAssignableFrom(source)) {
      return true;
    } else if (target.isPrimitive()) {
      if (source.isPrimitive()) {
        return TypeUtilities.isSubtype(source, target);
      } else {
        final var source0 = TypeUtilities.getPrimitiveType(source);
        return source0 != null && TypeUtilities.isSubtype(source0, target);
      }
    } else if (TypeUtilities.isWrapperType(target)) {
      return TypeUtilities.isSubtype(source, TypeUtilities.getPrimitiveType(target));
    } else {
      return false;
    }
  }
}
