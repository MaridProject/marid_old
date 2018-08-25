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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class RoutingActions {

  protected final HashMap<String, RoutingAction> map = new HashMap<>();
  protected final LinkedList<Function<String, RoutingAction>> funcs = new LinkedList<>();

  public RoutingAction action(String name) {
    return Optional.ofNullable(map.get(name))
        .or(() -> funcs.stream()
            .map(f -> f.apply(name))
            .filter(Objects::nonNull)
            .findFirst()
        )
        .orElse(null);
  }
}
