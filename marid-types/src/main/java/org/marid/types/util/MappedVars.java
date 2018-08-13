/*-
 * #%L
 * marid-types
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

package org.marid.types.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public final class MappedVars {

  private final LinkedList<TypeVariable<?>> vars = new LinkedList<>();
  private final LinkedHashMap<TypeVariable<?>, Type> map = new LinkedHashMap<>();

  public void put(@NotNull TypeVariable<?> var, @NotNull Type type) {
    map.computeIfAbsent(var, v -> {
      vars.add(v);
      return type;
    });
  }

  @Nullable
  public Type get(@NotNull TypeVariable<?> var) {
    return map.get(var);
  }

  public Stream<TypeVariable<?>> vars() {
    return vars.stream();
  }

  public Stream<Type> types() {
    return vars.stream().map(map::get);
  }

  @NotNull
  public Stream<Map.Entry<TypeVariable<?>, Type>> entries() {
    return map.entrySet().stream();
  }

  public void forEach(@NotNull BiConsumer<TypeVariable<?>, Type> consumer) {
    vars.descendingIterator().forEachRemaining(v -> consumer.accept(v, map.get(v)));
  }

  @Override
  public String toString() {
    return entries().map(MappedVars::entryToString).collect(joining(",", "{", "}"));
  }

  private static String entryToString(@NotNull Map.Entry<TypeVariable<?>, Type> entry) {
    return entry.getKey() + "(" + entry.getKey().getGenericDeclaration() + "): " + entry.getValue();
  }
}
