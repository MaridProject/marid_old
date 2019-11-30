package org.marid.racks.net;

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
    final var winery = new Winery("test")
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
