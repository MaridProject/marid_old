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

import org.marid.runtime.annotation.BottleIn;
import org.marid.runtime.annotation.BottleOut;
import org.marid.runtime.annotation.Rack;
import org.marid.runtime.model.AbstractRack;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.StandardSocketOptions;

@Rack(title = "Server socket")
public class ServerSocketRack extends AbstractRack<ServerSocket> {

  public ServerSocketRack(@BottleIn(title = "Socket port to bind") int port,
                          @BottleIn(title = "Requested maximum length of the queue of incoming connections") int backlog,
                          @BottleIn(title = "Bind address", code = "bind") InetAddress bindAddress) {
    super(() -> new ServerSocket(port, backlog, bindAddress));
  }

  @BottleIn(code = "IBS")
  public void inputBufferSize(int size) throws IOException {
    instance.setOption(StandardSocketOptions.SO_RCVBUF, size);
  }

  @BottleIn(code = "OBS")
  public void outputBufferSize(int size) throws IOException {
    instance.setOption(StandardSocketOptions.SO_SNDBUF, size);
  }

  @BottleOut(title = "Actual port")
  public int port() {
    return instance.getLocalPort();
  }
}
