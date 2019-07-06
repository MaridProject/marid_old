package org.marid.app.web;

/*-
 * #%L
 * marid-ide-server
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

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.pac4j.core.client.Clients;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class UnauthorizedHandler implements HttpHandler {

  private final VelocityEngine engine;
  private final Clients clients;

  public UnauthorizedHandler(VelocityEngine engine, Clients clients) {
    this.engine = engine;
    this.clients = clients;
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    try (final var vm = new InputStreamReader(getClass().getResourceAsStream("/v/unauthorized.vm"), UTF_8)) {
      final var writer = new StringWriter();
      if (engine.evaluate(new VelocityContext(new HashMap<>(Map.of("clients", clients))), writer, "unauthorized", vm)) {
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
        exchange.setStatusCode(HttpURLConnection.HTTP_OK);

        final var bytes = ByteBuffer.wrap(writer.toString().getBytes(UTF_8));
        exchange.getResponseChannel().write(bytes);

        exchange.endExchange();
      }
    }
  }
}
