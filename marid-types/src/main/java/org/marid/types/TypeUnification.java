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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.marid.types.Types.isAssignableFrom;

public class TypeUnification {

  private TypeUnification() {
  }

  @NotNull
  public static Map<TypeVariable<?>, Type> resolve(@NotNull Type type) {
    final var map = new LinkedHashMap<TypeVariable<?>, Type>();
    resolveTypes(type, map);
    map.replaceAll((k, v) -> Types.substitute(v, map::get));
    return map;
  }

  static void resolveTypes(Type type, LinkedHashMap<TypeVariable<?>, Type> map) {
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
      for (final var gsi : raw.getGenericInterfaces()) {
        resolveTypes(gsi, map);
      }
    } else if (type instanceof Class<?>) {
      final var t = (Class<?>) type;
      final var gsc = t.getGenericSuperclass();
      if (gsc != null) {
        resolveTypes(gsc, map);
      }
      for (final var gsi : t.getGenericInterfaces()) {
        resolveTypes(gsi, map);
      }
    }
  }

  @NotNull
  public static List<@NotNull Type> commonTypes(@NotNull Type... types) {
    return Stream.concat(
        Arrays.stream(types).flatMap(t -> TypeStreams.superclasses(t)
            .filter(s -> Arrays.stream(types).filter(type -> type != t).allMatch(type -> isAssignableFrom(s, type)))
        ),
        Arrays.stream(types).flatMap(t -> TypeStreams.interfaces(t)
            .filter(s -> Arrays.stream(types).filter(type -> type != t).allMatch(type -> isAssignableFrom(s, type)))
        )
    ).collect(TypeStreams.absorber());
  }

  @NotNull
  public static Type commonType(@NotNull Type... types) {
    return WildcardTypes.wildcardIfNecessary(commonTypes(types));
  }
}