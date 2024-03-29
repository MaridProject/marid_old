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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("normal")
class WildcardTypesTest extends TypeSugar {

  @Test
  void equality() throws ReflectiveOperationException {
    final var getClass = Object.class.getMethod("getClass");
    final var rv = (ParameterizedType) getClass.getGenericReturnType();
    final var expected = (WildcardType) rv.getActualTypeArguments()[0];
    assertEquals(w(), expected);
    assertEquals(expected, w());
  }
}
