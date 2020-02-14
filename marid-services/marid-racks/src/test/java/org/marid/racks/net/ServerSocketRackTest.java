package org.marid.racks.net;

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
import org.marid.runtime.model.LiteralImpl;
import org.marid.runtime.model.NullImpl;
import org.marid.runtime.model.CellarImpl;
import org.marid.runtime.model.InputImpl;
import org.marid.runtime.model.RackImpl;
import org.marid.runtime.model.WineryImpl;

import java.net.StandardSocketOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.marid.runtime.model.LiteralImpl.Type.INT;

@Tag("normal")
class ServerSocketRackTest {

  @Test
  void constructor() throws Exception {
    final int inputBufferSize = 100_001;
    final var winery = new WineryImpl("g", "test", "1.0")
      .addCellar(
        new CellarImpl("cellar1")
          .addRack(
            new RackImpl("serverSocket1", ServerSocketRack.class.getName())
              .addArguments(
                new LiteralImpl(INT, "0"),
                new LiteralImpl(INT, "10"),
                new NullImpl()
              )
              .addInputs(
                new InputImpl("inputBufferSize", new LiteralImpl(INT, Integer.toString(inputBufferSize)))
              )
          )
          .addRack(
            new RackImpl("serverSocket2", ServerSocketRack.class.getName())
              .addArguments(
                new LiteralImpl(INT, "0"),
                new LiteralImpl(INT, "11"),
                new NullImpl()
              )
          )
      );
    try (final var runtime = new WineryRuntime(winery)) {
      runtime.start();

      final var cellar1 = runtime.getCellar("cellar1");
      final var serverSocket1 = cellar1.getRack("serverSocket1");
      final var serverSocket2 = cellar1.getRack("serverSocket2");

      assertTrue(serverSocket1.getInstance() instanceof ServerSocketRack);
      assertTrue(serverSocket2.getInstance() instanceof ServerSocketRack);

      final var rack1 = (ServerSocketRack) serverSocket1.getInstance();
      final var rack2 = (ServerSocketRack) serverSocket2.getInstance();

      assertEquals(inputBufferSize, rack1.getServerSocket().getOption(StandardSocketOptions.SO_RCVBUF));
    }
  }
}
