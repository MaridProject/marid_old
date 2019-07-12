package org.marid.app.web;

/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
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
