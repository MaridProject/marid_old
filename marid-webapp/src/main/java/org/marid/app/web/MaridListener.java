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

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ApplicationRunner;
import org.marid.app.common.Directories;
import org.marid.ui.webide.base.MainUI;
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
    application.addEntryPoint("/app", () -> new MainUI(context), Map.of());
  }
}
