package org.marid.racks;

import org.marid.runtime.annotation.Constant;
import org.marid.runtime.annotation.Constants;

@Constants
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
