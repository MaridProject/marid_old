package org.marid.app.web.router;

/*-
 * #%L
 * marid-ide-server
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

import java.util.*;
import java.util.function.Function;

public class RoutingPaths {

  protected final LinkedList<Function<String, RoutingPath>> funcs = new LinkedList<>();
  protected final HashMap<String, RoutingPath> map = new LinkedHashMap<>();

  RoutingPath get(String name) {
    return Optional.ofNullable(map.get(name))
        .or(() -> funcs.stream()
            .map(f -> f.apply(name))
            .filter(Objects::nonNull)
            .findFirst())
        .orElse(null);
  }
}