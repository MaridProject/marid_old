package org.marid.processors;

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
