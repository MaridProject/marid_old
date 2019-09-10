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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public interface ParameterizedTypes {

  @NotNull
  static ParameterizedType parameterizedType(@NotNull Class<?> raw, @NotNull Type... parameters) {
    return new ParameterizedTypeImpl(raw, parameters, raw.getDeclaringClass());
  }

  @NotNull
  static Type parameterized(@NotNull Class<?> raw, @NotNull Type... parameters) {
    if (parameters.length == 0 && raw.getTypeParameters().length == 0) {
      return raw;
    } else {
      return parameterizedType(raw, parameters);
    }
  }

  @NotNull
  static ParameterizedType parameterizedTypeWithOwner(@NotNull Class<?> raw, @Nullable Type owner, @NotNull Type... parameters) {
    return new ParameterizedTypeImpl(raw, parameters, owner);
  }

  @NotNull
  static Type parameterizedWithOwner(@NotNull Class<?> raw, @Nullable Type owner, @NotNull Type... parameters) {
    if (parameters.length == 0 && raw.getTypeParameters().length == 0 && owner == raw.getDeclaringClass()) {
      return raw;
    } else {
      return parameterizedTypeWithOwner(raw, owner, parameters);
    }
  }

  @NotNull
  static ParameterizedType parameterizedTypeFromClass(@NotNull Class<?> type) {
    return new ParameterizedTypeImpl(type, type.getTypeParameters(), type.getDeclaringClass());
  }

  @NotNull
  static Type parameterizedFromClass(@NotNull Class<?> type) {
    final var vars = type.getTypeParameters();
    return vars.length > 0 ? parameterizedTypeFromClass(type) : type;
  }

  @NotNull
  static Stream<Type> parameters(@NotNull ParameterizedType type) {
    return Arrays.stream(type.getActualTypeArguments());
  }

  @NotNull
  static Optional<Type> owner(@NotNull ParameterizedType type) {
    return Optional.ofNullable(type.getOwnerType());
  }
}
