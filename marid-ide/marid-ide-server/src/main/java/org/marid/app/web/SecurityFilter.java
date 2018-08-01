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

import org.marid.spring.annotation.PrototypeScoped;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.decision.AlwaysUseSessionProfileStorageDecision;
import org.pac4j.core.exception.HttpAction;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@PrototypeScoped
public class SecurityFilter implements Filter {

  private final Config config;
  private final DefaultSecurityLogic<Boolean, J2EContext> logic = new DefaultSecurityLogic<>() {
    @Override
    protected HttpAction unauthorized(J2EContext context, List<Client> currentClients) {
      return HttpAction.redirect(context, "/public/unauthorized.html");
    }
  };
  private final SecurityGrantedAccessAdapter<Boolean, J2EContext> authorize = (ctx, profiles, params) -> true;

  public SecurityFilter(Config config) {
    this.config = config;
    this.logic.setProfileStorageDecision(new AlwaysUseSessionProfileStorageDecision());
  }

  @Override
  public void doFilter(ServletRequest q, ServletResponse r, FilterChain c) throws IOException, ServletException {
    final var context = new J2EContext((HttpServletRequest) q, (HttpServletResponse) r);
    final var result = logic.perform(context, config, authorize, (code, ctx) -> false, null, "user", null, false);
    if (result) {
      c.doFilter(q, r);
    }
  }
}
