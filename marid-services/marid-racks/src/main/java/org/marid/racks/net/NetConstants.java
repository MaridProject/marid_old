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

import org.marid.runtime.annotation.Constant;
import org.marid.runtime.annotation.Constants;
import org.marid.runtime.annotation.Title;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Title("Network constants")
@Constants
public interface NetConstants {

  @Constant
  static InetAddress inetAddressFromBytes(byte[] address) throws UnknownHostException {
    return InetAddress.getByAddress(address);
  }

  @Constant
  static InetAddress inetAddressFromHostAndBytes(String host, byte[] address) throws UnknownHostException {
    return InetAddress.getByAddress(host, address);
  }

  @Constant
  static InetAddress inetAddressFromHost(String host) throws UnknownHostException {
    return InetAddress.getByName(host);
  }

  @Constant
  static InetAddress anyLocalAddress() {
    return new InetSocketAddress(0).getAddress();
  }

  @Constant
  static InetAddress loopbackAddress() {
    return InetAddress.getLoopbackAddress();
  }

  @Constant
  static InetSocketAddress socketAddress(String host, int port) {
    return new InetSocketAddress(host, port);
  }

  @Constant
  static InetSocketAddress socketAddressUnresolved(String host, int port) {
    return InetSocketAddress.createUnresolved(host, port);
  }

  @Constant
  static InetSocketAddress socketAddressFromInetAddress(InetAddress address, int port) {
    return new InetSocketAddress(address, port);
  }
}
