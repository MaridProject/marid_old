package org.marid.collections;

/*-
 * #%L
 * marid-util
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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

import java.util.function.Predicate;

public interface MaridArrays {

  @SafeVarargs
  static <E> boolean allMatch(@NotNull Predicate<E> predicate, @NotNull E... args) {
    for (final E e : args) {
      if (!predicate.test(e)) {
        return false;
      }
    }
    return true;
  }

  @SafeVarargs
  static <E> boolean noneMatch(@NotNull Predicate<E> predicate, @NotNull E... args) {
    for (final E e : args) {
      if (predicate.test(e)) {
        return false;
      }
    }
    return true;
  }

  @SafeVarargs
  static <E> boolean anyMatch(@NotNull Predicate<E> predicate, @NotNull E... args) {
    for (final E e : args) {
      if (predicate.test(e)) {
        return true;
      }
    }
    return false;
  }
}
