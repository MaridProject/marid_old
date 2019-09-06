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

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

public interface Classes {

  Class<?>[] EMPTY_CLASSES = {};

  static boolean isPublic(Class<?> type) {
    try {
      MethodHandles.publicLookup().accessClass(type);
      return true;
    } catch (IllegalAccessException | SecurityException e) {
      return false;
    }
  }

  static Stream<Method> methods(Class<?> type) {
    return Arrays.stream(type.getMethods())
        .filter(m -> isPublic(m.getDeclaringClass()));
  }

  static Stream<Field> fields(Class<?> type) {
    return Arrays.stream(type.getFields())
        .filter(f -> isPublic(f.getDeclaringClass()));
  }
}
