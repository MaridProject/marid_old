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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public interface WildcardTypes {

  @NotNull
  static WildcardType wildcardTypeUpperBounds(@NotNull Type... upperBounds) {
    return new WildcardTypeImpl(upperBounds.length == 0 ? TypeUtils.ONLY_OBJECT : upperBounds, Types.EMPTY_TYPES);
  }

  @NotNull
  static WildcardType wildcardTypeLowerBounds(@NotNull Type... lowerBounds) {
    return new WildcardTypeImpl(Types.EMPTY_TYPES, lowerBounds);
  }

  @NotNull
  static WildcardType wildcardType(@NotNull Type[] upperBounds, @NotNull Type[] lowerBounds) {
    return new WildcardTypeImpl(upperBounds.length == 0 ? TypeUtils.ONLY_OBJECT : upperBounds, lowerBounds);
  }

  @NotNull
  static Stream<@NotNull Type> upperBounds(@NotNull WildcardType type) {
    return Arrays.stream(type.getUpperBounds());
  }

  @NotNull
  static Stream<@NotNull Type> lowerBounds(@NotNull WildcardType type) {
    return Arrays.stream(type.getLowerBounds());
  }

  @NotNull
  static Type wildcardIfNecessary(@NotNull Collection<@NotNull Type> types) {
    return types.size() == 1 ? types.iterator().next() : wildcardTypeUpperBounds(types.toArray(Type[]::new));
  }

  @NotNull
  static Stream<@NotNull Type> flatten(@NotNull Type type) {
    return type instanceof WildcardType ? upperBounds((WildcardType) type) : Stream.of(type);
  }

  static int compare(@NotNull WildcardType t1, @NotNull WildcardType t2) {
    final var lb1 = t1.getLowerBounds();
    final var lb2 = t2.getLowerBounds();
    int c = Integer.compare(lb1.length, lb2.length);
    if (c != 0) {
      return c;
    }
    final var ub1 = t1.getUpperBounds();
    final var ub2 = t2.getUpperBounds();
    c = Integer.compare(ub1.length, ub2.length);
    if (c != 0) {
      return c;
    }
    for (int i = 0; i < lb1.length; i++) {
      c = Types.compare(lb1[i], lb2[i]);
      if (c != 0) {
        return c;
      }
    }
    for (int i = 0; i < ub1.length; i++) {
      c = Types.compare(ub1[i], ub2[i]);
      if (c != 0) {
        return c;
      }
    }
    return 0;
  }
}
