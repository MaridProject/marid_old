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
import org.marid.runtime.model.ArgumentLiteral;
import org.marid.runtime.model.ArgumentNull;
import org.marid.runtime.model.Cellar;
import org.marid.runtime.model.Input;
import org.marid.runtime.model.Rack;
import org.marid.runtime.model.Winery;

import java.net.StandardSocketOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.marid.runtime.model.ArgumentLiteral.Type.INT;

@Tag("normal")
class ServerSocketRackTest {

  @Test
  void constructor() throws Exception {
    final int inputBufferSize = 100_001;
    final var winery = new Winery("g", "test", "1.0")
      .addCellar(
        new Cellar("cellar1")
          .addRack(
            new Rack("serverSocket1", ServerSocketRack.class.getName())
              .addArguments(
                new ArgumentLiteral(INT, "0"),
                new ArgumentLiteral(INT, "10"),
                new ArgumentNull()
              )
              .addInputs(
                new Input("inputBufferSize", new ArgumentLiteral(INT, Integer.toString(inputBufferSize)))
              )
          )
          .addRack(
            new Rack("serverSocket2", ServerSocketRack.class.getName())
              .addArguments(
                new ArgumentLiteral(INT, "0"),
                new ArgumentLiteral(INT, "11"),
                new ArgumentNull()
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
