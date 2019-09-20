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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TypeUnification {

  @NotNull
  public static Map<TypeVariable<?>, Type> resolve(@NotNull Type type) {
    final var map = new HashMap<TypeVariable<?>, Type>();
    resolveTypes(type, map);
    return Map.of();
  }

  static void resolveTypes(Type type, HashMap<TypeVariable<?>, Type> map) {
    if (type instanceof ParameterizedType) {
      final var t = (ParameterizedType) type;
      final var raw = (Class<?>) t.getRawType();
      final var args = t.getActualTypeArguments();
      final var vars = raw.getTypeParameters();
      for (int i = 0; i < vars.length; i++) {
        map.put(vars[i], args[i]);
      }
      final var gsc = raw.getGenericSuperclass();
      if (gsc != null) {
        resolveTypes(gsc, map);
      }
      for (final var gsi: raw.getGenericInterfaces()) {
        resolveTypes(gsi, map);
      }
    } else if (type instanceof Class<?>) {
      final var t = (Class<?>) type;
      final var gsc = t.getGenericSuperclass();
      if (gsc != null) {
        resolveTypes(gsc, map);
      }
      for (final var gsi: t.getGenericInterfaces()) {
        resolveTypes(gsi, map);
      }
    }
  }

  private static void resolveVars(Type left, Type right, TreeMap<TypeVariable<?>, Type> map) {
    if (Types.isGround(left)) {
      return;
    }
    if (left instanceof TypeVariable<?>) {
      final var l = (TypeVariable<?>) left;
      final var old = map.putIfAbsent(l, right);
      if (old == null || right.equals(old)) {
        return;
      }
      if (old instanceof TypeVariable<?>) {
        if (right instanceof TypeVariable<?>) {
          return;
        }
        resolveVars(old, right, map);
      }
    }
  }
}
