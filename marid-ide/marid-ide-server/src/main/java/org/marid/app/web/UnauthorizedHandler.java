package org.marid.app.web;

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
