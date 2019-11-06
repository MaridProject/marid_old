package org.marid.processors;

/*-
 * #%L
 * marid-processors
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

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import static java.lang.invoke.MethodHandles.publicLookup;

class Unsafer {

  public static final Lookup LOOKUP;
  static {
    try {
      final var unsafeClass = Class.forName("sun.misc.Unsafe");
      final var field = unsafeClass.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      final var unsafe = field.get(null);
      final var staticFieldOffsetMethod = publicLookup().findVirtual(
          unsafe.getClass(), "staticFieldOffset", MethodType.methodType(long.class, Field.class)
      );
      final var getObjectMethod = publicLookup().findVirtual(
          unsafe.getClass(), "getObject", MethodType.methodType(Object.class, Object.class, long.class)
      );

      final var privateLookup = Lookup.class.getDeclaredField("IMPL_LOOKUP");
      final var privateLookupAddr = (long) staticFieldOffsetMethod.invoke(unsafe, privateLookup);

      LOOKUP = (Lookup) getObjectMethod.invoke(unsafe, Lookup.class, privateLookupAddr);
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }
}
