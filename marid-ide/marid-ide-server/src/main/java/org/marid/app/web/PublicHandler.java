package org.marid.app.web;

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

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.marid.app.undertow.DeploymentManagerProvider;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;

@Component
public class PublicHandler extends PathHandler {

  public PublicHandler(DeploymentManagerProvider deploymentManagerProvider,
                       ClassPathResourceManager publicResourceManager,
                       PathResourceManager rwtResourceManager) throws ServletException {
    super(new ResourceHandler(publicResourceManager, new ResourceHandler(rwtResourceManager, deploymentManagerProvider.start())));
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    final var path = exchange.getRelativePath();

    if (path.equals("/")) {
      new RedirectHandler("/index.ide").handleRequest(exchange);
    } else {
      super.handleRequest(exchange);
    }
  }
}
