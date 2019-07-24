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
