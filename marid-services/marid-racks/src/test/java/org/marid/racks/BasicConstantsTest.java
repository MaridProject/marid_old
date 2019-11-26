package org.marid.racks;

/*-
 * #%L
 * marid-racks
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
import org.marid.runtime.internal.WineryRuntime;
import org.marid.runtime.model.ArgumentConstRef;
import org.marid.runtime.model.ArgumentLiteral;
import org.marid.runtime.model.Cellar;
import org.marid.runtime.model.CellarConstant;
import org.marid.runtime.model.Winery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.marid.runtime.model.ArgumentLiteral.Type.BYTE;
import static org.marid.runtime.model.ArgumentLiteral.Type.INT;

@Tag("normal")
class BasicConstantsTest {

  @Test
  void simple() throws Exception {
    final var winery = new Winery("testWinery")
        .addCellar(new Cellar("testCellar1")
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("byteConstant", byte.class), "c1")
                .addArg(new ArgumentLiteral(BYTE, "12"))
            )
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("intConstant", int.class), "c2")
                .addArg(new ArgumentLiteral(INT, "23"))
            )
        );
    try (final var runtime = new WineryRuntime("test", winery)) {
      runtime.start();

      final var testCellar1 = runtime.getCellar("testCellar1");

      assertNotNull(testCellar1);
      assertEquals((byte) 12, testCellar1.getConstant("c1"));
      assertEquals(23, testCellar1.getConstant("c2"));
    }
  }

  @Test
  void refs() throws Exception {
    final var winery = new Winery("testWinery")
        .addCellar(new Cellar("testCellar1")
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("byteConstant", byte.class), "c1")
                .addArg(new ArgumentLiteral(BYTE, "12"))
            )
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("intConstant", int.class), "c2")
                .addArg(new ArgumentLiteral(INT, "23"))
            )
        )
        .addCellar(new Cellar("testCellar2")
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("intConstant", int.class), "c1")
                .addArg(new ArgumentConstRef("testCellar1", "c2"))
            )
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("byteConstant", byte.class), "c2")
                .addArg(new ArgumentConstRef("testCellar1", "c1"))
            )
        );
    try (final var runtime = new WineryRuntime("test", winery)) {
      runtime.start();

      final var testCellar1 = runtime.getCellar("testCellar1");

      assertNotNull(testCellar1);
      assertEquals((byte) 12, testCellar1.getConstant("c1"));
      assertEquals(23, testCellar1.getConstant("c2"));

      final var testCellar2 = runtime.getCellar("testCellar2");

      assertNotNull(testCellar2);
      assertEquals((byte) 12, testCellar2.getConstant("c2"));
      assertEquals(23, testCellar2.getConstant("c1"));
    }
  }

  @Test
  void orderOfCellars() throws Exception {
    final var winery = new Winery("testWinery")
        .addCellar(new Cellar("testCellar2")
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("intConstant", int.class), "c1")
                .addArg(new ArgumentConstRef("testCellar1", "c2"))
            )
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("byteConstant", byte.class), "c2")
                .addArg(new ArgumentConstRef("testCellar1", "c1"))
            )
        )
        .addCellar(new Cellar("testCellar1")
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("byteConstant", byte.class), "c1")
                .addArg(new ArgumentLiteral(BYTE, "12"))
            )
            .addConstant(new CellarConstant(BasicConstants.class.getMethod("intConstant", int.class), "c2")
                .addArg(new ArgumentLiteral(INT, "23"))
            )
        );
    try (final var runtime = new WineryRuntime("test", winery)) {
      runtime.start();

      final var testCellar1 = runtime.getCellar("testCellar1");

      assertNotNull(testCellar1);
      assertEquals((byte) 12, testCellar1.getConstant("c1"));
      assertEquals(23, testCellar1.getConstant("c2"));

      final var testCellar2 = runtime.getCellar("testCellar2");

      assertNotNull(testCellar2);
      assertEquals((byte) 12, testCellar2.getConstant("c2"));
      assertEquals(23, testCellar2.getConstant("c1"));

      assertEquals(List.of("testCellar1", "testCellar2"), List.copyOf(runtime.getCellarNames()));
    }
  }
}
