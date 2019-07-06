package org.marid.app.web;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.springframework.stereotype.Component;

@Component
public class PublicHandler extends PathHandler {

  public PublicHandler(AuthHandler authHandler) {
    super(new ResourceHandler(new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "public/"), authHandler));
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    final var path = exchange.getRelativePath();
    switch (path) {
      case "/": {
        new RedirectHandler("/index.html").handleRequest(exchange);
        return;
      }
    }

    super.handleRequest(exchange);
  }
}
