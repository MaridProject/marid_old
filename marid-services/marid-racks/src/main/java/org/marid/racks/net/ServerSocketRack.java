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

import org.marid.io.function.IOSupplier;
import org.marid.runtime.annotation.Description;
import org.marid.runtime.annotation.Rack;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;

@Rack
@Description("Server socket")
public class ServerSocketRack implements Closeable {

  private final ServerSocket serverSocket;

  public ServerSocketRack(int port, int backlog, InetAddress bindAddress) throws IOException {
    if (bindAddress == null) {
      this.serverSocket = new ServerSocket(port, backlog);
    } else {
      this.serverSocket = new ServerSocket(port, backlog, bindAddress);
    }
  }

  public void setInputBufferSize(int size) throws IOException {
    serverSocket.setOption(StandardSocketOptions.SO_RCVBUF, size);
  }

  public void setOutputBufferSize(int size) throws IOException {
    serverSocket.setOption(StandardSocketOptions.SO_SNDBUF, size);
  }

  public void setSoBroadcast(boolean broadcast) throws IOException {
    serverSocket.setOption(StandardSocketOptions.SO_BROADCAST, broadcast);
  }

  public int getPort() {
    return serverSocket.getLocalPort();
  }

  public ServerSocket getServerSocket() {
    return serverSocket;
  }

  public IOSupplier<Socket> getSocketSupplier() {
    return serverSocket::accept;
  }

  @Override
  public void close() throws IOException {
    serverSocket.close();
  }
}
