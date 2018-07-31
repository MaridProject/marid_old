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
package org.marid.image;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.invoke.MethodHandles;

public interface Colors {

  @NotNull
  static Color color(@NotNull String value) {
    if (value.isEmpty()) {
      throw new IllegalArgumentException();
    } else {
      final char first = value.charAt(0);
      if (first == '#' || Character.isDigit(first)) {
        return Color.decode(value);
      } else {
        final var lookup = MethodHandles.publicLookup();
        try {
          final var mh = lookup.findStaticGetter(Color.class, value, Color.class);
          return (Color) mh.invokeExact();
        } catch (Throwable x) {
          throw new IllegalStateException(x);
        }
      }
    }
  }
}
