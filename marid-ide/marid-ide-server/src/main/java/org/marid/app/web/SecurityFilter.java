/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package org.marid.app.web;

import io.undertow.servlet.spec.HttpServletRequestImpl;
import org.marid.applib.security.MaridAccount;
import org.marid.spring.annotation.PrototypeScoped;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

@Component
@PrototypeScoped
public class SecurityFilter extends HttpFilter {

  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    final var context = new J2EContext(req, res);
    final var profileManager = new ProfileManager<>(context);
    final var profile = profileManager.get(true);
    if (profile.isPresent()) {
      final var checker = new DefaultAuthorizationChecker();
      if (checker.isAuthorized(context, singletonList(profile.get()), null, emptyMap())) {
        final var account = new MaridAccount(profile.get());
        final var exchange = ((HttpServletRequestImpl) req).getExchange();
        final var securityContext = exchange.getSecurityContext();

        securityContext.authenticationComplete(account, "oauth2", false);

        chain.doFilter(req, res);
        return;
      }
    }
    res.sendRedirect("/unauthorized.html");
  }
}
