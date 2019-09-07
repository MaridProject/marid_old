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

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TypeUtils {

  @NotNull
  static <T> Set<@NotNull T> add(@NotNull Set<@NotNull T> vars, @NotNull T var) {
    if (vars.contains(var)) {
      return vars;
    } else if (vars.isEmpty()) {
      return Collections.singleton(var);
    } else {
      final var it = vars.iterator();
      switch (vars.size()) {
        case 1: return Set.of(it.next(), var);
        case 2: return Set.of(it.next(), it.next(), var);
        case 3: return Set.of(it.next(), it.next(), it.next(), var);
        case 4: return Set.of(it.next(), it.next(), it.next(), it.next(), var);
        case 5: return Set.of(it.next(), it.next(), it.next(), it.next(), it.next(), var);
        case 6: return Set.of(it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), var);
        case 7: return Set.of(it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), var);
        case 8: return Set.of(it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), var);
        case 9: return Set.of(it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), it.next(), var);
        default: return Stream.concat(Stream.of(var), vars.stream()).collect(Collectors.toUnmodifiableSet());
      }
    }
  }
}
