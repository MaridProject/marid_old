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

import static java.util.stream.Collectors.joining;

final class WildcardTypeImpl implements WildcardType {

  private final Type[] upperBounds;
  private final Type[] lowerBounds;

  WildcardTypeImpl(@NotNull Type[] upperBounds, @NotNull Type[] lowerBounds) {
    this.upperBounds = upperBounds;
    this.lowerBounds = lowerBounds;
  }

  @Override
  public Type[] getUpperBounds() {
    return upperBounds.length == 0 ? upperBounds : upperBounds.clone();
  }

  @Override
  public Type[] getLowerBounds() {
    return lowerBounds.length == 0 ? lowerBounds : lowerBounds.clone();
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(upperBounds) ^ Arrays.hashCode(lowerBounds);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof WildcardType) {
      return Arrays.equals(upperBounds, ((WildcardType) obj).getUpperBounds())
          && Arrays.equals(lowerBounds, ((WildcardType) obj).getLowerBounds());
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    if (lowerBounds.length == 0) {
      if (upperBounds.length == 0 || Arrays.equals(upperBounds, TypeUtils.ONLY_OBJECT)) {
        return "?";
      } else {
        return Arrays.stream(upperBounds).map(Type::getTypeName).collect(joining(" & ", "? extends ", ""));
      }
    } else {
      if (upperBounds.length == 0 || Arrays.equals(upperBounds, TypeUtils.ONLY_OBJECT)) {
        return Arrays.stream(lowerBounds).map(Type::getTypeName).collect(joining(" & ", "? super ", ""));
      } else {
        return Arrays.stream(upperBounds).map(Type::getTypeName).collect(joining(" & ", "? extends ",
            Arrays.stream(lowerBounds).map(Type::getTypeName).collect(joining(" & ", " super ", ""))));
      }
    }
  }
}
