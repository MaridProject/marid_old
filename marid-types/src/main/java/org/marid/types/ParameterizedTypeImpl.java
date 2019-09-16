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

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

final class ParameterizedTypeImpl implements ParameterizedType {

  private final Class<?> rawType;
  private final Type[] actualTypeArguments;
  private final Type ownerType;

  ParameterizedTypeImpl(@NotNull Class<?> rawType, @NotNull Type[] actualTypeArguments, @Nullable Type ownerType) {
    this.rawType = rawType;
    this.actualTypeArguments = actualTypeArguments;
    this.ownerType = ownerType;

    if (actualTypeArguments.length == 0) {
      throw new MalformedParameterizedTypeException("Illegal number of type arguments for " + rawType.getName()
          + ": should be more than 0 but the actual is " + actualTypeArguments.length
      );
    }

    final var vars = rawType.getTypeParameters();
    if (vars.length != actualTypeArguments.length) {
      throw new MalformedParameterizedTypeException("Illegal number of type arguments of " + rawType.getName()
          + ": should be " + vars.length
          + " but the actual is " + actualTypeArguments.length);
    }

    if (ownerType != null) {
      if (ownerType instanceof Class<?>) {
        if (!ownerType.equals(rawType.getDeclaringClass())) {
          throw new MalformedParameterizedTypeException("Illegal owner for " + rawType.getName()
              + ": should be " + rawType.getDeclaringClass()
              + " but the actual is " + ownerType
          );
        }
      } else {
        if (!(ownerType instanceof ParameterizedType)) {
          throw new MalformedParameterizedTypeException(
              "Illegal owner type: must be Class<?> or ParameterizedType but the actual is " + ownerType
          );
        }
        if (!((ParameterizedType) ownerType).getRawType().equals(rawType.getDeclaringClass())) {
          throw new MalformedParameterizedTypeException("Illegal owner for " + rawType.getName()
              + ": should be " + rawType.getDeclaringClass()
              + " but the actual is " + ownerType
          );
        }
      }
    }
  }

  @Override
  public Type[] getActualTypeArguments() {
    return actualTypeArguments.clone();
  }

  @Override
  public Type getRawType() {
    return rawType;
  }

  @Override
  public Type getOwnerType() {
    return ownerType;
  }

  @Override
  public int hashCode() {
    return rawType.hashCode() ^ Arrays.hashCode(actualTypeArguments) ^ Objects.hashCode(ownerType);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj instanceof ParameterizedType) {
      final var that = (ParameterizedType) obj;

      return Objects.equals(ownerType, that.getOwnerType())
          && Objects.equals(rawType, that.getRawType())
          && Arrays.equals(actualTypeArguments, that.getActualTypeArguments());
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    if (ownerType != null) {
      sb.append(ownerType.getTypeName());
      sb.append('$');
      sb.append(rawType.getSimpleName());
    } else {
      sb.append(rawType.getName());
    }

    sb.append('<');
    for (int i = 0; i < actualTypeArguments.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(actualTypeArguments[i].getTypeName());
    }
    sb.append('>');

    return sb.toString();
  }
}
