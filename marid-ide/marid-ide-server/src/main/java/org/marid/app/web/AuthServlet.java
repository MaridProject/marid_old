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

import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

@Component
public class AuthServlet extends HttpServlet {

  private final Config config;

  public AuthServlet(Config config) {
    this.config = config;
  }

  @Override
  protected void doGet(HttpServletRequest q, HttpServletResponse r) throws IOException {
    final var path = q.getServletPath();
    final var clientString = path.substring(1);
    final var client = (IndirectClient<?, ?>) config.getClients().findClient(clientString);
    final var context = new J2EContext(q, r);
    final var profileManager = new ProfileManager<>(context);
    final var profile = profileManager.getAll(true).stream()
        .filter(p -> !p.isExpired())
        .findFirst();
    if (profile.isPresent()) {
      final var authorizationChecker = new DefaultAuthorizationChecker();
      if (authorizationChecker.isAuthorized(context, singletonList(profile.get()), null, emptyMap())) {
        r.sendRedirect("/index.ide");
      } else {
        r.sendRedirect("/unauthorized.html");
      }
    } else {
      final var ajaxResolver = new DefaultAjaxRequestResolver();
      if (!ajaxResolver.isAjax(context)) {
        q.getSession(true).setAttribute(Pac4jConstants.REQUESTED_URL, context.getFullRequestURL());
      }
      client.redirect(context);
    }
  }
}
