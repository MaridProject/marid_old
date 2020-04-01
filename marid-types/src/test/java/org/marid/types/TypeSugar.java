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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Executable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeSugar {

  @NotNull
  protected static Type p(@NotNull Class<?> raw, @NotNull Type... params) {
    if (params.length == 0) {
      return ParameterizedTypes.parameterized(raw);
    } else {
      return ParameterizedTypes.parameterizedType(raw, params);
    }
  }

  @NotNull
  protected static Type p(@NotNull Class<?> raw, int index) {
    return raw.getTypeParameters()[index];
  }

  @NotNull
  protected static Type p(@NotNull Type type, int index) {
    if (type instanceof Class<?>) {
      return p((Class<?>) type, index);
    } else {
      return ((ParameterizedType) type).getActualTypeArguments()[index];
    }
  }

  @NotNull
  protected static ParameterizedType po(@NotNull Class<?> raw, @Nullable Type owner, @NotNull Type... params) {
    return ParameterizedTypes.parameterizedTypeWithOwner(raw, owner, params);
  }

  @NotNull
  protected static GenericArrayType a(@NotNull Type componentType) {
    return GenericArrayTypes.genericArrayType(componentType);
  }

  @NotNull
  protected static WildcardType w() {
    return WildcardTypes.wildcardType(Types.EMPTY_TYPES, Types.EMPTY_TYPES);
  }

  @NotNull
  protected static WildcardType wu(@NotNull Type... upper) {
    return WildcardTypes.wildcardTypeUpperBounds(upper);
  }

  @NotNull
  protected static WildcardType wl(@NotNull Type... lower) {
    return WildcardTypes.wildcardTypeLowerBounds(lower);
  }

  @NotNull
  protected static TypeVariable<?> v(@NotNull Class<?> type, int index) {
    return type.getTypeParameters()[index];
  }

  @NotNull
  protected static TypeVariable<?> v(@NotNull ReflectiveSupplier<? extends Executable> method, int index) {
    return method.get().getTypeParameters()[index];
  }

  @NotNull
  protected static Var v(@NotNull GenericDeclaration decl, @NotNull String name) {
    return new Var(decl, name);
  }

  @NotNull
  protected static Type b(@NotNull Type type, int index) {
    if (type instanceof TypeVariable<?>) {
      return ((TypeVariable<?>) type).getBounds()[index];
    } else {
      return ((WildcardType) type).getUpperBounds()[index];
    }
  }

  @NotNull
  protected static Type lb(@NotNull Type type, int index) {
    return ((WildcardType) type).getLowerBounds()[index];
  }

  @NotNull
  protected static Type ct(@NotNull Type type) {
    return ((GenericArrayType) type).getGenericComponentType();
  }

  public static Map<Var, Type> prettyMap(@NotNull Map<TypeVariable<?>, Type> binding) {
    return binding.entrySet().stream()
        .collect(Collectors.toMap(
            e -> new Var(e.getKey()),
            e -> Var.convert(e.getValue()),
            (e1, e2) -> e2,
            LinkedHashMap::new)
        );
  }
}
