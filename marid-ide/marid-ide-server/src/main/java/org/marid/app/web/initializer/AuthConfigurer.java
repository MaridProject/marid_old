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
package org.marid.app.web.initializer;

import org.marid.app.web.AuthServlet;
import org.marid.app.web.CallbackServlet;
import org.marid.app.web.LogoutServlet;
import org.marid.app.web.SecurityFilter;
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
      r.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST), false, "maridServlet");
      r.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/ide/*", "/rest/*");
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
