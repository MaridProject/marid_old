package org.marid.runtime.cellars;

import java.io.Closeable;
import java.util.Objects;
import java.util.stream.Stream;

public class Cellar1 {

  public static class Rack1 {

    private static final Closeable INSTANCE = () -> {
    };

    public static Closeable instance() {
      return INSTANCE;
    }
  }

  public static class Rack2 {

    private static final Closeable INSTANCE = () -> {
    };

    public static Closeable instance() {
      return INSTANCE;
    }
  }

  public static class Rack3 {

    private static final Closeable INSTANCE = Stream.of(Rack1.instance(), Rack1.instance())
        .filter(Objects::isNull)
        .findFirst()
        .orElse(() -> {
        });

    public static Closeable instance() {
      return INSTANCE;
    }
  }

  public static class Rack4 {

    private static final Closeable INSTANCE = Stream.of(Rack1.instance(), Rack2.instance())
        .filter(Objects::isNull)
        .findFirst()
        .orElse(() -> {
        });


    public static Closeable instance() {
      return INSTANCE;
    }
  }

  public static class Rack5 {

    private static final Object INSTANCE = Stream.of(Rack3.instance(), Rack4.instance())
        .filter(Objects::isNull)
        .findFirst()
        .orElseThrow(IllegalCallerException::new);

    public static Object instance() {
      return INSTANCE;
    }
  }
}
