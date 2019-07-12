/*-
 * #%L
 * marid-ide-server
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

import org.marid.app.web.initializer.ServletContextConfigurer;
import org.marid.applib.spring.event.HttpSessionCreatedEvent;
import org.marid.applib.spring.event.HttpSessionDestroyedEvent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.LinkedList;
import java.util.List;

import static java.util.logging.Level.INFO;
import static org.marid.logging.Log.log;

@Component
public class MaridListener implements ServletContextListener, HttpSessionListener {

  private final ObjectProvider<List<? extends ServletContextConfigurer>> configurersProvider;
  private final List<ServletContextConfigurer> configurers = new LinkedList<>();
  private final GenericApplicationContext context;

  public MaridListener(ObjectProvider<List<? extends ServletContextConfigurer>> configurersProvider,
                       GenericApplicationContext context) {
    this.configurersProvider = configurersProvider;
    this.context = context;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    for (final var configurer : configurersProvider.getObject()) {
      log(INFO, "Configuring {0}", configurer.getClass().getName());
      configurer.start(sce.getServletContext());
      if (configurer.isStopNeeded()) {
        configurers.add(configurer);
      }
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    for (final var configurer : configurers) {
      log(INFO, "Stopping {0}", configurer.getClass().getName());
      configurer.stop(sce.getServletContext());
    }
  }

  @Override
  public void sessionCreated(HttpSessionEvent se) {
    context.publishEvent(new HttpSessionCreatedEvent(se.getSession()));
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    context.publishEvent(new HttpSessionDestroyedEvent(se.getSession()));
  }
}
