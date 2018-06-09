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
package org.marid.app.web;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ApplicationRunner;
import org.marid.app.common.Directories;
import org.marid.ui.webide.base.boot.MainEntryPoint;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

@Component
public class MaridListener implements ServletContextListener, ApplicationConfiguration {

  private final GenericApplicationContext context;
  private final String directory;

  private ApplicationRunner runner;

  public MaridListener(GenericApplicationContext context, Directories directories) {
    this.context = context;
    this.directory = directories.getRwtDir().toString();
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    sce.getServletContext().setAttribute(ApplicationConfiguration.RESOURCE_ROOT_LOCATION, directory);
    runner = new ApplicationRunner(this, sce.getServletContext());
    runner.start();
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    try {
      runner.stop();
    } finally {
      runner = null;
    }
  }

  @Override
  public void configure(Application application) {
    application.setOperationMode(Application.OperationMode.SWT_COMPATIBILITY);
    application.addEntryPoint("/main.marid", () -> new MainEntryPoint(context), Map.of());
  }
}
