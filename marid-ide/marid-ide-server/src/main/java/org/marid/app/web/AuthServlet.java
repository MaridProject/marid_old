/*-
 * #%L
 * marid-ide-server
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

import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.decision.AlwaysUseSessionProfileStorageDecision;
import org.pac4j.core.exception.HttpAction;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class AuthServlet extends HttpServlet {

  private final Config config;
  private final DefaultSecurityLogic<Void, J2EContext> securityLogic = new DefaultSecurityLogic<>() {
    @Override
    protected HttpAction unauthorized(J2EContext context, List<Client> currentClients) {
      return HttpAction.redirect(context, "/unauthorized.html");
    }
  };

  public AuthServlet(Config config) {
    this.config = config;
    this.securityLogic.setProfileStorageDecision(new AlwaysUseSessionProfileStorageDecision());
  }

  @Override
  protected void doGet(HttpServletRequest q, HttpServletResponse r) {
    final var path = q.getServletPath();
    final var client = path.substring(1);

    final SecurityGrantedAccessAdapter<Void, J2EContext> granted = (ctx, profiles, params) -> {
      r.sendRedirect("/index.html");
      return null;
    };

    securityLogic.perform(new J2EContext(q, r), config, granted, (code, context) -> null, client, "user", null, false);
  }
}
