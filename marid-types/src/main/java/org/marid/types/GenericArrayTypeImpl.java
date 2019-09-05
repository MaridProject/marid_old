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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

final class GenericArrayTypeImpl implements GenericArrayType {

  private final Type genericComponentType;

  GenericArrayTypeImpl(@NotNull Type genericComponentType) {
    this.genericComponentType = genericComponentType;
  }

  @Override
  public Type getGenericComponentType() {
    return genericComponentType;
  }

  @Override
  public int hashCode() {
    return genericComponentType.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof GenericArrayType) {
      return genericComponentType.equals(((GenericArrayType) obj).getGenericComponentType());
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return genericComponentType.getTypeName() + "[]";
  }
}
