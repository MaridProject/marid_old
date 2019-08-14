package org.marid.function;

import java.util.function.Supplier;

@FunctionalInterface
public interface SafeSupplier<T> extends Supplier<T> {

  @Override
  default T get() {
    try {
      return getUnsafe();
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  T getUnsafe() throws Exception;
}
