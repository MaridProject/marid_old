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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

public interface TypeStreams {

  @NotNull
  static Stream<@NotNull Type> superclasses(@NotNull Type type) {
    if (type instanceof Class<?>) {
      final var t = (Class<?>) type;
      if (t.isInterface()) {
        return Stream.empty();
      }
    } else if (type instanceof ParameterizedType) {
      final var t = (ParameterizedType) type;
      final var raw = (Class<?>) t.getRawType();
      if (raw.isInterface()) {
        return Stream.of();
      }
    }
    return superclasses(type, Types.EMPTY_TYPES, new LinkedHashMap<>(0));
  }

  static Stream<Type> superclasses(Type type, Type[] passed, LinkedHashMap<TypeVariable<?>, Type> bindings) {
    if (type instanceof Class<?>) {
    } if (type instanceof GenericArrayType) {
      return Stream.of(type, Object.class);
    }

    return Stream.of();
  }
}
