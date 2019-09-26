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
import java.util.stream.IntStream;
import java.util.stream.Stream;

class TypeUtils {

  static final Type[] ONLY_OBJECT = {Object.class};

  @NotNull
  static Type[] add(@NotNull Type[] types, @NotNull Type type) {
    final int len = types.length;
    if (len == 0) {
      return new Type[]{type};
    } else {
      for (final Type t : types) {
        if (t.equals(type)) {
          return types;
        }
      }
      final var newTypes = Arrays.copyOf(types, len + 1, Type[].class);
      newTypes[len] = type;
      return newTypes;
    }
  }

  static boolean contains(Type[] types, Type type) {
    for (final Type t : types) {
      if (t.equals(type)) {
        return true;
      }
    }
    return false;
  }

  static Stream<Type[]> combinations(Type[][] types) {
    final int count = Arrays.stream(types).mapToInt(t -> t.length).reduce(1, (a, e) -> a * e);
    return IntStream.range(0, count).mapToObj(n -> {
      final var r = new Type[types.length];
      int d = count;
      for (int i = types.length - 1; i >= 0; i--) {
        d /= types[i].length;
        r[i] = types[i][n / d];
        n %= d;
      }
      return r;
    });
  }
}
