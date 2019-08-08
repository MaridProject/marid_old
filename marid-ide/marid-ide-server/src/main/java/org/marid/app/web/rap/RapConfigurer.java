package org.marid.app.web.rap;

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

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ApplicationRunner;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.marid.app.common.Directories;
import org.marid.app.web.ServletContextConfigurer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class RapConfigurer implements ServletContextConfigurer {

  private final RapAppConfig appConfig;
  private final Directories directories;

  private ApplicationRunner runner;

  public RapConfigurer(RapAppConfig appConfig, Directories directories) {
    this.appConfig = appConfig;
    this.directories = directories;
  }

  @Override
  public void start(ServletContext context) {
    context.setAttribute(ApplicationConfiguration.RESOURCE_ROOT_LOCATION, directories.getRwtDir().toAbsolutePath().toString());

    runner = new ApplicationRunner(appConfig, context);
    runner.start();

    final var r = context.addServlet("ideServlet", new RWTServlet());
    r.addMapping("*.ide");
    r.setLoadOnStartup(6);
    r.setAsyncSupported(true);
  }

  @Override
  public void stop(ServletContext context) {
      runner.stop();
  }

  @Override
  public boolean isStopNeeded() {
    return true;
  }
}
