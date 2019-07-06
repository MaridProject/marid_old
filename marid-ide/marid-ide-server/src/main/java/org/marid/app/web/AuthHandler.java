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
import io.undertow.server.handlers.RedirectHandler;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.decision.AlwaysUseSessionProfileStorageDecision;
import org.pac4j.undertow.context.UndertowWebContext;
import org.pac4j.undertow.profile.UndertowProfileManager;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthHandler implements HttpHandler {

  private final Config config;
  private final MainHandler mainHandler;
  private final UnauthorizedHandler unauthorizedHandler;
  private final DefaultCallbackLogic<Void, UndertowWebContext> callbackLogic = new DefaultCallbackLogic<>();
  private final DefaultLogoutLogic<Void, UndertowWebContext> logoutLogic = new DefaultLogoutLogic<>();
  private final DefaultSecurityLogic<Boolean, UndertowWebContext> securityLogic = new DefaultSecurityLogic<>();
  private final Map<String, HttpHandler> authHandlers;

  public AuthHandler(Config config, MainHandler mainHandler, UnauthorizedHandler unauthorizedHandler) {
    this.config = config;
    this.mainHandler = mainHandler;
    this.unauthorizedHandler = unauthorizedHandler;

    securityLogic.setProfileStorageDecision(new AlwaysUseSessionProfileStorageDecision());
    securityLogic.setProfileManagerFactory(UndertowProfileManager::new);
    securityLogic.setErrorUrl("/securityError");

    logoutLogic.setProfileManagerFactory(UndertowProfileManager::new);
    logoutLogic.setErrorUrl("/logoutError");

    callbackLogic.setErrorUrl("/callbackError");
    callbackLogic.setProfileManagerFactory(UndertowProfileManager::new);

    authHandlers = Map.copyOf(config.getClients().getClients().stream()
        .collect(Collectors.toMap(client -> "/" + client.getName(), client -> e -> {
          final var result = check(e, client.getName());
          if (result) {
            new RedirectHandler("/index.html").handleRequest(e);
          }
        }))
    );
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    final var path = exchange.getRelativePath();
    switch (path) {

      case "/callback": {
        callbackLogic.perform(new UndertowWebContext(exchange), config, (code, ctx) -> null, "/index.html", true, false, null, null);
        exchange.endExchange();
        break;
      }

      case "/logout": {
        logoutLogic.perform(new UndertowWebContext(exchange), config, (code, ctx) -> null, "/unauthorized", null, true, false, false);
        exchange.endExchange();
        break;
      }

      case "/logoutError":
      case "/callbackError":
      case "/securityError": {
        System.out.println(exchange);
        exchange.endExchange();
        break;
      }

      case "/unauthorized": {
        unauthorizedHandler.handleRequest(exchange);
        break;
      }

      default: {
        final var authHandler = authHandlers.get(path);

        if (authHandler != null) {
          authHandler.handleRequest(exchange);
        } else {
          if (check(exchange, null)) {
            mainHandler.handleRequest(exchange);
          } else {
            switch (path) {
              case "/index.html":
              case "/login":
                unauthorizedHandler.handleRequest(exchange);
                break;
              default:
                exchange.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
                exchange.endExchange();
                break;
            }
          }
        }

        break;
      }
    }
  }

  private boolean check(HttpServerExchange exchange, String client) {
    final var ctx = new UndertowWebContext(exchange);
    return securityLogic.perform(ctx, config, (c, pf, ps) -> true, (code, c) -> false, client, "user", null, false);
  }
}
