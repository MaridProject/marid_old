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

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.stream.Stream;

public interface TypeVariables {

  @NotNull
  static Stream<@NotNull Type> bounds(@NotNull TypeVariable<?> var) {
    return Arrays.stream(var.getBounds());
  }

  @NotNull
  static Stream<@NotNull TypeVariable<?>> from(@NotNull Type type) {
    return extract(type).distinct();
  }

  private static Stream<TypeVariable<?>> extract(Type type) {
    if (type instanceof TypeVariable<?>) {
      return Stream.of((TypeVariable<?>) type);
    } else if (type instanceof WildcardType) {
      return Stream.concat(
          Arrays.stream(((WildcardType) type).getUpperBounds()).flatMap(TypeVariables::extract),
          Arrays.stream(((WildcardType) type).getLowerBounds()).flatMap(TypeVariables::extract)
      );
    } else if (type instanceof GenericArrayType) {
      return extract(((GenericArrayType) type).getGenericComponentType());
    } else if (type instanceof ParameterizedType) {
      return Arrays.stream(((ParameterizedType) type).getActualTypeArguments()).flatMap(TypeVariables::extract);
    } else {
      return Stream.empty();
    }
  }
}
