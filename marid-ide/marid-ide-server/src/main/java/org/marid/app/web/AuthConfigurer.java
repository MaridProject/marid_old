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
package org.marid.app.web;

import org.marid.spring.annotation.PrototypeScoped;
import org.pac4j.core.client.Clients;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;

@Component
@PrototypeScoped
@Order(3)
public class AuthConfigurer implements ServletContextConfigurer {

  private final CallbackServlet callbackServlet;
  private final AuthServlet authServlet;
  private final LogoutServlet logoutServlet;
  private final SecurityFilter securityFilter;
  private final Clients clients;

  public AuthConfigurer(CallbackServlet callbackServlet,
                        AuthServlet authServlet,
                        LogoutServlet logoutServlet,
                        SecurityFilter securityFilter,
                        Clients clients) {
    this.callbackServlet = callbackServlet;
    this.authServlet = authServlet;
    this.logoutServlet = logoutServlet;
    this.securityFilter = securityFilter;
    this.clients = clients;
  }

  @Override
  public void start(ServletContext context) {
    {
      final var r = context.addServlet("callbackServlet", callbackServlet);
      r.addMapping("/callback");
      r.setLoadOnStartup(1);
      r.setAsyncSupported(true);
    }
    {
      final var r = context.addServlet("authServlet", authServlet);
      r.setAsyncSupported(true);
      r.setLoadOnStartup(2);
      clients.findAllClients().forEach(c -> r.addMapping("/" + c.getName()));
    }
    {
      final var r = context.addServlet("logoutServlet", logoutServlet);
      r.setAsyncSupported(true);
      r.setLoadOnStartup(3);
      r.addMapping("/logout");
    }
    {
      final var r = context.addFilter("securityFilter", securityFilter);
      r.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST), false, "ideServlet");
      r.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/ide/*");
      r.setAsyncSupported(true);
    }
  }

  @Override
  public void stop(ServletContext context) {
  }

  @Override
  public boolean isStopNeeded() {
    return false;
  }
}
