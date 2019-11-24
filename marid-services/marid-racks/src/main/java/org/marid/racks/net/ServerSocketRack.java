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

import org.marid.runtime.annotation.In;
import org.marid.runtime.annotation.Out;
import org.marid.runtime.annotation.Rack;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.StandardSocketOptions;

@Rack(title = "Server socket")
public class ServerSocketRack {

  private final ServerSocket serverSocket;

  public ServerSocketRack(
      @In(code = "PORT", title = "Socket port to bind") int port,
      @In(code = "BACKLOG", title = "Requested maximum length of the queue of incoming connections") int backlog,
      @In(code = "BIND", title = "Bind address") InetAddress bindAddress
  ) throws IOException {
    this.serverSocket = new ServerSocket(port, backlog, bindAddress);
  }

  @In(code = "IBS", title = "Input buffer size")
  public void setInputBufferSize(int size) throws IOException {
    serverSocket.setOption(StandardSocketOptions.SO_RCVBUF, size);
  }

  @In(code = "OBS", title = "Output buffer size")
  public void setOutputBufferSize(int size) throws IOException {
    serverSocket.setOption(StandardSocketOptions.SO_SNDBUF, size);
  }

  @Out(code = "PORT", title = "Actual port")
  public int getPort() {
    return serverSocket.getLocalPort();
  }

  @Out(code = "SOCK", title = "Server socket")
  public ServerSocket getServerSocket() {
    return serverSocket;
  }
}
