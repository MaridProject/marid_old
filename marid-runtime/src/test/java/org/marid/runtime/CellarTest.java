package org.marid.runtime;

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
