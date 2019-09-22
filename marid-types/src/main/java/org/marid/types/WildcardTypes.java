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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.stream.Stream;

public interface WildcardTypes {

  @NotNull
  static WildcardType wildcardTypeUpperBounds(@NotNull Type... upperBounds) {
    return new WildcardTypeImpl(upperBounds.length == 0 ? TypeUtils.ONLY_OBJECT : upperBounds, Types.EMPTY_TYPES);
  }

  @NotNull
  static WildcardType wildcardTypeLowerBounds(@NotNull Type... lowerBounds) {
    return new WildcardTypeImpl(Types.EMPTY_TYPES, lowerBounds);
  }

  @NotNull
  static WildcardType wildcardType(@NotNull Type[] upperBounds, @NotNull Type[] lowerBounds) {
    return new WildcardTypeImpl(upperBounds.length == 0 ? TypeUtils.ONLY_OBJECT : upperBounds, lowerBounds);
  }

  @NotNull
  static Stream<@NotNull Type> upperBounds(@NotNull WildcardType type) {
    return Arrays.stream(type.getUpperBounds());
  }

  @NotNull
  static Stream<@NotNull Type> lowerBounds(@NotNull WildcardType type) {
    return Arrays.stream(type.getLowerBounds());
  }
}
