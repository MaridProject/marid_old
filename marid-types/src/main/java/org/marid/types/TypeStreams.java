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

import java.util.Arrays;
import java.util.stream.Stream;

public interface TypeStreams {

  static Stream<Class<?>> superclasses(Class<?> type) {
    if (type.isInterface()) {
      return Stream.empty();
    } else {
      return Stream.of(type).flatMap(TypeStreams::superclasses0);
    }
  }

  private static Stream<Class<?>> superclasses0(Class<?> type) {
    return Stream.concat(
        Stream.of(type),
        Stream.ofNullable(type.getSuperclass()).flatMap(TypeStreams::superclasses0)
    );
  }

  static Stream<Class<?>> interfaces(Class<?> type) {
    return (type.isInterface()
        ? Stream.of(type).flatMap(TypeStreams::interfaces0)
        : superclasses(type).flatMap(t -> Arrays.stream(t.getInterfaces())).flatMap(TypeStreams::interfaces0)
    ).distinct()
        .sorted((i1, i2) -> {
          if (i1.equals(i2)) {
            return 0;
          } else if (i1.isAssignableFrom(i2)) {
            return 1;
          } else if (i2.isAssignableFrom(i1)) {
            return -1;
          } else {
            return 0;
          }
        });
  }

  private static Stream<Class<?>> interfaces0(Class<?> itf) {
    return Stream.concat(
        Stream.of(itf),
        Arrays.stream(itf.getInterfaces()).flatMap(TypeStreams::interfaces0)
    );
  }
}
