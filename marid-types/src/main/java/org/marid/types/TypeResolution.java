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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.marid.types.Types.isAssignableFrom;

public interface TypeResolution {

  @NotNull
  static LinkedHashMap<TypeVariable<?>, Type> resolveVars(@NotNull Type type) {
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
  static List<@NotNull Type> commonTypes(@NotNull Supplier<@NotNull Stream<? extends @NotNull Type>> typesSupplier) {
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
  static Type commonType(@NotNull Supplier<@NotNull Stream<? extends @NotNull Type>> typesSupplier) {
    return WildcardTypes.wildcard(commonTypes(typesSupplier));
  }

  @NotNull
  static Type resolve(@NotNull Type source, @NotNull Consumer<BiConsumer<@NotNull Type, @NotNull Type>> binder) {
    final var map = new LinkedHashMap<TypeVariable<?>, LinkedHashSet<Type>>();
    final var resolved = resolveVars(source);
    binder.accept((to, from) -> resolve(to, from, map, resolved, Types.EMPTY_TYPES));
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

  private static void resolve(Type to,
                              Type from,
                              LinkedHashMap<TypeVariable<?>, LinkedHashSet<Type>> map,
                              LinkedHashMap<TypeVariable<?>, Type> resolved,
                              Type[] passed) {
    if (Types.isGround(to) || !Types.isGround(from)) {
      return;
    }
    if (to instanceof TypeVariable<?>) {
      resolve((TypeVariable<?>) to, from, map, resolved, passed);
    } else if (from instanceof WildcardType) {
      for (final var b : ((WildcardType) from).getUpperBounds()) {
        resolve(to, b, map, resolved, passed);
      }
      for (final var b : ((WildcardType) from).getLowerBounds()) {
        resolve(to, b, map, resolved, passed);
      }
    } else if (to instanceof ParameterizedType) {
      resolve((ParameterizedType) to, from, map, resolved, passed);
    } else if (to instanceof GenericArrayType) {
      resolve((GenericArrayType) to, from, map, resolved, passed);
    } else if (to instanceof WildcardType) {
      for (final var b : ((WildcardType) to).getUpperBounds()) {
        resolve(b, from, map, resolved, passed);
      }
      for (final var b : ((WildcardType) to).getLowerBounds()) {
        resolve(b, from, map, resolved, passed);
      }
    }
  }

  private static void resolve(TypeVariable<?> to,
                              Type from,
                              LinkedHashMap<TypeVariable<?>, LinkedHashSet<Type>> map,
                              LinkedHashMap<TypeVariable<?>, Type> resolved,
                              Type[] passed) {
    final var resolvedVar = resolved.get(to);
    if (resolvedVar != null && !resolvedVar.equals(to)) {
      resolve(resolvedVar, from, map, resolved, passed);
      return;
    }
    final var newPassed = TypeUtils.add(passed, to);
    if (newPassed.length == passed.length) {
      return;
    }
    map.computeIfAbsent(to, f -> new LinkedHashSet<>()).add(from);
    for (final var b : to.getBounds()) {
      resolve(to, b, map, resolved, newPassed);
    }
  }

  private static void resolve(ParameterizedType to,
                              Type from,
                              LinkedHashMap<TypeVariable<?>, LinkedHashSet<Type>> map,
                              LinkedHashMap<TypeVariable<?>, Type> resolved,
                              Type[] passed) {
    final var raw = (Class<?>) to.getRawType();
    final var vars = raw.getTypeParameters();
    final var args = to.getActualTypeArguments();
    assert vars.length == args.length;
    final var fromMap = resolveVars(from);
    for (int i = 0; i < vars.length; i++) {
      final var var = vars[i];
      final var actual = fromMap.get(var);
      if (actual == null) {
        continue;
      }
      final var arg = args[i];
      resolve(arg, actual, map, resolved, passed);
    }
  }

  private static void resolve(GenericArrayType to,
                              Type from,
                              LinkedHashMap<TypeVariable<?>, LinkedHashSet<Type>> map,
                              LinkedHashMap<TypeVariable<?>, Type> resolved,
                              Type[] passed) {
    if (from instanceof Class<?>) {
      final var f = (Class<?>) from;
      if (f.isArray()) {
        resolve(to.getGenericComponentType(), f.getComponentType(), map, resolved, passed);
      }
    } else if (from instanceof GenericArrayType) {
      resolve(to.getGenericComponentType(), ((GenericArrayType) from).getGenericComponentType(), map, resolved, passed);
    }
  }
}
