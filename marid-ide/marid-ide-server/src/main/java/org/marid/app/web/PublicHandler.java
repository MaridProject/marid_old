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

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.marid.app.undertow.DeploymentManagerProvider;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;

@Component
public class PublicHandler extends PathHandler {

  public PublicHandler(DeploymentManagerProvider deploymentManagerProvider) throws ServletException {
    super(new ResourceHandler(
        new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "public/"),
        deploymentManagerProvider.start()
    ));
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    final var path = exchange.getRelativePath();
    switch (path) {
      case "/": {
        new RedirectHandler("/ide").handleRequest(exchange);
        return;
      }
    }

    super.handleRequest(exchange);
  }
}
