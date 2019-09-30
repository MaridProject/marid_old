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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.marid.types.Types.isAssignableFrom;

public class TypeResolution {

  private TypeResolution() {
  }

  @NotNull
  public static LinkedHashMap<TypeVariable<?>, Type> resolveVars(@NotNull Type type) {
    final var map = new LinkedHashMap<TypeVariable<?>, Type>();
    resolveVars(type, map);
    map.replaceAll((k, v) -> Types.substitute(v, map::get));
    return map;
  }

  static void resolveVars(Type type, LinkedHashMap<TypeVariable<?>, Type> map) {
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
        resolveVars(gsc, map);
      }
      for (final var gsi : raw.getGenericInterfaces()) {
        resolveVars(gsi, map);
      }
    } else if (type instanceof Class<?>) {
      final var t = (Class<?>) type;
      final var gsc = t.getGenericSuperclass();
      if (gsc != null) {
        resolveVars(gsc, map);
      }
      for (final var gsi : t.getGenericInterfaces()) {
        resolveVars(gsi, map);
      }
    }
  }

  @NotNull
  public static List<@NotNull Type> commonTypes(@NotNull Type... types) {
    return commonTypes(() -> Arrays.stream(types));
  }

  @NotNull
  public static List<@NotNull Type> commonTypes(@NotNull Supplier<@NotNull Stream<@NotNull Type>> typesSupplier) {
    return Stream.concat(
        typesSupplier.get().flatMap(t -> TypeStreams.superclasses(t)
            .filter(s -> typesSupplier.get().filter(type -> type != t).allMatch(type -> isAssignableFrom(s, type)))
        ),
        typesSupplier.get().flatMap(t -> TypeStreams.interfaces(t)
            .filter(s -> typesSupplier.get().filter(type -> type != t).allMatch(type -> isAssignableFrom(s, type)))
        )
    ).collect(TypeStreams.absorber());
  }

  @NotNull
  public static Type commonType(@NotNull Type... types) {
    return commonType(() -> Arrays.stream(types));
  }

  @NotNull
  public static Type commonType(@NotNull Supplier<@NotNull Stream<@NotNull Type>> typesSupplier) {
    return WildcardTypes.wildcard(commonTypes(typesSupplier));
  }

  @NotNull
  public static Type resolve(@NotNull Type source, @NotNull Consumer<BiConsumer<@NotNull Type, @NotNull Type>> binder) {
    final var map = new LinkedHashMap<TypeVariable<?>, LinkedHashSet<Type>>();
    final var resolved = resolveVars(source);
    binder.accept((from, to) -> resolve(from, to, map, resolved, Types.EMPTY_TYPES));
    resolved.replaceAll((v, t) -> {
      final var set = map.get(v);
      if (set != null) {
        return commonType(set::stream);
      } else {
        return t;
      }
    });
    return Types.substitute(source, resolved::get);
  }

  private static void resolve(Type from,
                              Type to,
                              LinkedHashMap<TypeVariable<?>, LinkedHashSet<Type>> map,
                              LinkedHashMap<TypeVariable<?>, Type> resolved,
                              Type[] passed) {
    if (!Types.isGround(to) || Types.isGround(from)) {
      return;
    }
    if (from instanceof TypeVariable<?>) {
      resolve((TypeVariable<?>) from, to, map, resolved, passed);
    }
  }

  private static void resolve(TypeVariable<?> from,
                              Type to,
                              LinkedHashMap<TypeVariable<?>, LinkedHashSet<Type>> map,
                              LinkedHashMap<TypeVariable<?>, Type> resolved,
                              Type[] passed) {
    final var resolvedVar = resolved.get(from);
    if (resolvedVar != null) {
      resolve(resolvedVar, to, map, resolved, passed);
      return;
    }
    final var newPassed = TypeUtils.add(passed, from);
    if (newPassed.length == passed.length) {
      return;
    }
    map.computeIfAbsent(from, f -> new LinkedHashSet<>()).add(to);
    for (final var b : from.getBounds()) {
      resolve(b, to, map, resolved, newPassed);
    }
  }
}
