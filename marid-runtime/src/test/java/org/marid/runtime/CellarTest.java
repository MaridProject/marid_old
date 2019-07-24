package org.marid.runtime;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.marid.runtime.cellars.Cellar1;

@Tag("normal")
class CellarTest {

  @Test
  void test() {
    try {
      Cellar1.Rack5.instance();
    } catch (Throwable e) {
      print(e);
    }
  }

  private static void print(Throwable throwable) {
    for (final var e : throwable.getStackTrace()) {
      System.out.println(e);
    }
    if (throwable.getCause() != null) {
      print(throwable.getCause());
    }
  }
}
