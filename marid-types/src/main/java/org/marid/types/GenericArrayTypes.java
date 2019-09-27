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

public interface GenericArrayTypes {

  @NotNull
  static GenericArrayType genericArrayType(@NotNull Type component) {
    return new GenericArrayTypeImpl(component);
  }

  @NotNull
  static Type genericArray(@NotNull Type component) {
    if (component instanceof Class<?>) {
      return Classes.arrayClass((Class<?>) component);
    } else {
      return genericArrayType(component);
    }
  }

  static int compare(@NotNull GenericArrayType t1, @NotNull GenericArrayType t2) {
    return Types.compare(t1.getGenericComponentType(), t2.getGenericComponentType());
  }
}
