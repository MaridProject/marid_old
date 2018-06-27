/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.app.web.initializer;

import com.vaadin.flow.server.VaadinSession;
import org.marid.app.web.vaadin.IdeInstantiator;
import org.marid.app.web.vaadin.IdeServlet;
import org.marid.spring.annotation.PrototypeScoped;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
@PrototypeScoped
@Order(6)
public class IdeServletConfigurer implements ServletContextConfigurer {

  private final IdeServlet servlet;
  private final IdeInstantiator instantiator;

  public IdeServletConfigurer(IdeServlet servlet, IdeInstantiator instantiator) {
    this.servlet = servlet;
    this.instantiator = instantiator;
  }

  @Override
  public void start(ServletContext context) {
    final var r = context.addServlet("ideServlet", servlet);
    r.setLoadOnStartup(4);
    r.setAsyncSupported(true);
    r.addMapping("/app/*", "/VAADIN/*", "/frontend/*");

    context.addListener(new HttpSessionListener() {
      @Override
      public void sessionCreated(HttpSessionEvent se) {
      }

      @Override
      public void sessionDestroyed(HttpSessionEvent se) {
        final var vaadinSessions = VaadinSession.getAllSessions(se.getSession());
        for (final var vaadinSession : vaadinSessions) {
          for (final var ui : vaadinSession.getUIs()) {
            instantiator.close(ui);
          }
        }
      }
    });
  }

  @Override
  public void stop(ServletContext context) {
  }

  @Override
  public boolean isStopNeeded() {
    return false;
  }
}
