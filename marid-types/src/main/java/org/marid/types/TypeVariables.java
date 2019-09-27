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

  static int compare(@NotNull TypeVariable<?> v1, @NotNull TypeVariable<?> v2) {
    final var d1 = v1.getGenericDeclaration();
    final var d2 = v2.getGenericDeclaration();
    if (d1 instanceof Executable && d2 instanceof Executable) {
      final var e1 = (Executable) d1;
      final var e2 = (Executable) d2;
      int c = Classes.compare(e1.getDeclaringClass(), e2.getDeclaringClass());
      if (c != 0) {
        return c;
      }
      c = e1.getName().compareTo(e2.getName());
      if (c != 0) {
        return c;
      }
      final var a1 = e1.getParameterTypes();
      final var a2 = e2.getParameterTypes();
      c = Integer.compare(a1.length, a2.length);
      if (c != 0) {
        return c;
      }
      for (int i = 0; i < a1.length; i++) {
        c = Classes.compare(a1[i], a2[i]);
        if (c != 0) {
          return c;
        }
      }
      return 0;
    } else if (d1 instanceof Class<?> && d2 instanceof Class<?>) {
      final int c = Classes.compare((Class<?>) d1, (Class<?>) d2);
      if (c != 0) {
        return c;
      }
    } else {
      final int c = d1.toString().compareTo(d2.toString());
      if (c != 0) {
        return c;
      }
    }
    return v1.getName().compareTo(v2.getName());
  }
}
