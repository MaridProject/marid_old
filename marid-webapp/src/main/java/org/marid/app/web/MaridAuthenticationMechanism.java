/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
package org.marid.app.web;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.impl.SecurityContextFactoryImpl;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.util.Headers;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.decision.AlwaysUseSessionProfileStorageDecision;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class MaridAuthenticationMechanism implements AuthenticationMechanism {

  private final Config config;
  private final DefaultSecurityLogic<Boolean, J2EContext> logic = new DefaultSecurityLogic<>();

  public MaridAuthenticationMechanism(Config config) {
    this.config = config;
    logic.setProfileStorageDecision(new AlwaysUseSessionProfileStorageDecision());
  }

  @Override
  public AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
    if (exchange.getRelativePath().startsWith("/app")) {
      final var servletContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      final var context = new J2EContext(servletContext.getOriginalRequest(), servletContext.getOriginalResponse()) {
        @Override
        public void setResponseStatus(int code) {
          getResponse().setStatus(code);
        }
      };
      final var result = logic.perform(context, config, (ctx, profiles, params) -> {
        securityContext.authenticationComplete(new MaridAccount(profiles), "MARID", false);
        return true;
      }, (code, ctx) -> false, null, "user", null, false);
      if (result) {
        return AuthenticationMechanismOutcome.AUTHENTICATED;
      } else {
        return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      }
    } else {
      return AuthenticationMechanismOutcome.AUTHENTICATED;
    }
  }

  @Override
  public ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
    exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
    exchange.getResponseHeaders().add(Headers.CACHE_CONTROL, "no-cache, max-age=0, must-revalidate, no-store");
    exchange.getResponseHeaders().add(Headers.PRAGMA, "no-cache");
    exchange.getResponseHeaders().add(Headers.EXPIRES, "0");
    exchange.getResponseHeaders().add(Headers.LOCATION, "/public/unauthorized.html");
    exchange.setStatusCode(HttpServletResponse.SC_TEMPORARY_REDIRECT);

    return new ChallengeResult(true, HttpServletResponse.SC_TEMPORARY_REDIRECT);
  }

  public void initialize(DeploymentInfo deploymentInfo) {
    deploymentInfo.setInitialSecurityWrapper(handler -> {
      final var mode = deploymentInfo.getAuthenticationMode();
      final var amh = new AuthenticationMechanismsHandler(handler, List.of(this));
      return new SecurityInitialHandler(mode, null, "MARID", SecurityContextFactoryImpl.INSTANCE, amh);
    });
  }
}
