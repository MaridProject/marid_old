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
import java.util.Arrays;
import java.util.Comparator;

class TypeUtils {

  @NotNull
  static Type[] add(@NotNull Type[] types, @NotNull Type type) {
    if (types.length == 0) {
      return new Type[] {type};
    } else {
      int index = Arrays.binarySearch(types, type, Comparator.comparingInt(Object::hashCode));
      final int len = types.length;
      if (index < 0) {
        index = -(index + 1);
      } else {
        if (types[index].equals(type)) {
          return types;
        }
      }
      final var newTypes = Arrays.copyOf(types, len + 1);
      if (index < len) {
        System.arraycopy(types, index, newTypes, index + 1, len - index);
      }
      newTypes[index] = type;
      return newTypes;
    }
  }

  static boolean contains(@NotNull Type[] types, @NotNull Type type) {
    final int index = Arrays.binarySearch(types, type, Comparator.comparingInt(Object::hashCode));
    return index >= 0 && types[index].equals(type);
  }
}
