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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Executable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class TypeSugar {

  @NotNull
  public static ParameterizedType p(@NotNull Class<?> raw, @NotNull Type... params) {
    return Types.parameterizedType(raw, params);
  }

  @NotNull
  public static ParameterizedType po(@NotNull Class<?> raw, @Nullable Type owner, @NotNull Type... params) {
    return Types.parameterizedTypeWithOwner(raw, owner, params);
  }

  @NotNull
  public static GenericArrayType a(@NotNull Type componentType) {
    return Types.genericArrayType(componentType);
  }

  @NotNull
  public static WildcardType wu(@NotNull Type... upper) {
    return Types.wildcardTypeUpperBounds(upper);
  }

  @NotNull
  public static WildcardType wl(@NotNull Type... lower) {
    return Types.wildcardTypeLowerBounds(lower);
  }

  @NotNull
  public static TypeVariable<?> v(@NotNull Class<?> type, int index) {
    return type.getTypeParameters()[index];
  }

  @NotNull
  public static TypeVariable<?> v(@NotNull ReflectiveSupplier<? extends Executable> method, int index) {
    return method.getSafe().getTypeParameters()[index];
  }
}
