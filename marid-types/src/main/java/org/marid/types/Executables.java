package org.marid.types;

/*-
 * #%L
 * marid-types
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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

import java.lang.reflect.Executable;

public interface Executables {

  static int compare(@NotNull Executable e1, @NotNull Executable e2) {
    if (e1.equals(e2)) {
      return 0;
    }

    final var c1 = e1.getDeclaringClass();
    final var c2 = e2.getDeclaringClass();

    int c = Classes.compare(c1, c2);
    if (c != 0) {
      return c;
    }

    c = Integer.compare(e1.getParameterCount(), e2.getParameterCount());
    if (c != 0) {
      return c;
    }

    c = e1.getName().compareTo(e2.getName());
    if (c != 0) {
      return c;
    }

    final var args1 = e1.getParameterTypes();
    final var args2 = e2.getParameterTypes();
    for (int i = 0; i < args1.length; i++) {
      c = Classes.compare(args1[i], args2[i]);
      if (c != 0) {
        return c;
      }
    }

    return 0;
  }
}
