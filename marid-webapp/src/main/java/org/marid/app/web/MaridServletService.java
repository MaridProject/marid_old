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

import com.vaadin.server.*;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultLogoutLogic;

import javax.servlet.ServletException;

public class MaridServletService extends VaadinServletService {

  public MaridServletService(MaridServlet servlet, DeploymentConfiguration configuration) throws ServiceException {
    super(servlet, configuration);
  }

  @Override
  public MaridServlet getServlet() {
    return (MaridServlet) super.getServlet();
  }

  @Override
  public void handleRequest(VaadinRequest request, VaadinResponse response) throws ServiceException {
    final var session = request.getWrappedSession(false);
    final var pathInfo = request.getPathInfo();

    if (session != null && pathInfo != null) {

      switch (pathInfo) {
        case "/logout": {
          final var config = getServlet().getContext().getBean(Config.class);
          final var r = (VaadinServletRequest) request;
          final var q = (VaadinServletResponse) response;
          final var context = new J2EContext(r, q);

          final var logic = new DefaultLogoutLogic<Void, J2EContext>();
          logic.perform(context, config, (code, ctx) -> null, "/app", null, true, false, false);

          /*
          try {
            r.logout();
          } catch (ServletException x) {
            throw new ServiceException(x);
          }*/

          return;
        }
      }
    }

    super.handleRequest(request, response);
  }
}
