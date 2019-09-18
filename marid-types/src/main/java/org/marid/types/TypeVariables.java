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

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.stream.Stream;

public interface TypeVariables {

  @NotNull
  static Stream<@NotNull Type> bounds(@NotNull TypeVariable<?> var) {
    return Arrays.stream(var.getBounds());
  }

  static int compare(@NotNull TypeVariable<?> v1, @NotNull TypeVariable<?> v2) {
    final var gd1 = v1.getGenericDeclaration();
    final var gd2 = v2.getGenericDeclaration();

    int c = Integer.compare(genericDeclKey(gd1), genericDeclKey(gd2));
    if (c != 0) {
      return c;
    }

    if (gd1 instanceof Class<?>) {
      final var c1 = (Class<?>) gd1;
      final var c2 = (Class<?>) gd2;

      c = Classes.fullCompare(c1, c2);
      if (c != 0) {
        return c;
      }
    }

    if (gd1 instanceof Executable) {
      final var e1 = (Executable) gd1;
      final var e2 = (Executable) gd2;

      c = Classes.fullCompare(e1.getDeclaringClass(), e2.getDeclaringClass());
      if (c != 0) {
        return c;
      }

      c = e1.getName().compareTo(e2.getName());
      if (c != 0) {
        return c;
      }

      c = Integer.compare(e1.getParameterCount(), e2.getParameterCount());
      if (c != 0) {
        return c;
      }

      final var ps1 = e1.getParameterTypes();
      final var ps2 = e2.getParameterTypes();
      for (int i = 0; i < ps1.length; i++) {
        c = Classes.fullCompare(ps1[i], ps2[i]);
        if (c != 0) {
          return c;
        }
      }
    }

    return v1.getName().compareTo(v2.getName());
  }

  private static int genericDeclKey(GenericDeclaration genericDeclaration) {
    if (genericDeclaration instanceof Class<?>) {
      return 0;
    } else if (genericDeclaration instanceof Constructor<?>) {
      return 1;
    } else {
      return 2;
    }
  }
}
