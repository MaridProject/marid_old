package org.marid.racks.basic;

/*-
 * #%L
 * marid-racks
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

import org.marid.runtime.annotation.Constant;
import org.marid.runtime.annotation.Constants;
import org.marid.runtime.annotation.Title;

@Constants
@Title("Basic constants")
public interface BasicConstants {

  @Constant
  static int intConstant(int value) {
    return value;
  }

  @Constant
  static int intUnsigned(long value) {
    return (int) (value & 0xFFFF_FFFFL);
  }

  @Constant
  static long longConstant(long value) {
    return value;
  }

  @Constant
  static long longUnsigned(String value) {
    return Long.parseUnsignedLong(value);
  }

  @Constant
  static short shortConstant(short value) {
    return value;
  }

  @Constant
  static short shortUnsigned(int value) {
    return (short) (value & 0xFFFF);
  }

  @Constant
  static char charConstant(char value) {
    return value;
  }

  @Constant
  static byte byteConstant(byte value) {
    return value;
  }

  @Constant
  static byte byteUnsigned(short value) {
    return (byte) (value & 0xFF);
  }
}
