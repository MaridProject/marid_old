package org.marid.runtime.cellars;

/*-
 * #%L
 * marid-runtime
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

import org.marid.runtime.Destructor;
import org.marid.runtime.Rack;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

public class Cellar1 {

  public static class Rack1 extends Rack {

    private static final Closeable INSTANCE = () -> {
    };

    static {
      register();
    }
  }

  public static class Rack2 extends Rack {

    private static final Closeable INSTANCE = () -> {
    };

    static {
      register();
    }

    @Destructor(order = 1)
    public static void close() throws IOException {
      INSTANCE.close();
    }
  }

  public static class Rack3 extends Rack {

    private static final Closeable INSTANCE = Stream.of(Rack1.INSTANCE, Rack1.INSTANCE)
        .filter(Objects::isNull)
        .findFirst()
        .orElse(() -> {
        });

    static {
      register();
    }

    @Destructor(order = 1)
    public static void close() throws IOException {
      INSTANCE.close();
    }
  }

  public static class Rack4 extends Rack {

    private static final Closeable INSTANCE = Stream.of(Rack1.INSTANCE, Rack2.INSTANCE)
        .filter(Objects::isNull)
        .findFirst()
        .orElse(() -> {
          throw new IOException("4");
        });

    static {
      register();
    }

    @Destructor(order = 1)
    public static void close() throws IOException {
      INSTANCE.close();
    }
  }

  public static class Rack5 extends Rack {

    public static final Closeable INSTANCE = Stream.of(Rack3.INSTANCE, Rack4.INSTANCE)
        .filter(Objects::isNull)
        .findFirst()
        .orElse(() -> {
          throw new IOException("5");
        });

    @Destructor(order = 1)
    public static void close() throws IOException {
      INSTANCE.close();
    }
  }
}
