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

import org.marid.mvc.MvcConfiguration;
import org.marid.spring.annotation.PrototypeScoped;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;

@Component
@PrototypeScoped
@Order(10)
public class DispatcherServletConfigurer implements ServletContextConfigurer {

  private final DispatcherServlet servlet;
  private final AnnotationConfigWebApplicationContext webContext;

  public DispatcherServletConfigurer(GenericApplicationContext context) {
    webContext = new AnnotationConfigWebApplicationContext();
    webContext.setParent(context);
    webContext.setAllowBeanDefinitionOverriding(false);
    webContext.setAllowCircularReferences(false);
    webContext.setId("web");
    webContext.setDisplayName("maridWebContext");
    webContext.register(MvcConfiguration.class);

    servlet = new DispatcherServlet(webContext);
  }

  @Override
  public void start(ServletContext context) {
    webContext.setServletContext(context);

    final var r = context.addServlet("dispatcherServlet", servlet);
    r.addMapping("/rest/*");
    r.setLoadOnStartup(9);
    r.setAsyncSupported(true);
  }

  @Override
  public void stop(ServletContext context) {
    servlet.destroy();
  }

  @Override
  public boolean isStopNeeded() {
    return true;
  }
}
