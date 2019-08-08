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
package org.marid.app.web;

import org.marid.applib.spring.event.HttpSessionCreatedEvent;
import org.marid.applib.spring.event.HttpSessionDestroyedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(MaridListener.class);

  private final ObjectProvider<List<? extends ServletContextConfigurer>> configurersProvider;
  private final List<ServletContextConfigurer> configurers = new LinkedList<>();
  private final GenericApplicationContext context;

  public MaridListener(ObjectProvider<List<? extends ServletContextConfigurer>> configurersProvider, GenericApplicationContext context) {
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
    LOGGER.info("Created {}", se.getSession());
    context.publishEvent(new HttpSessionCreatedEvent(se.getSession()));
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    LOGGER.info("Destroyed {}", se.getSession());
    context.publishEvent(new HttpSessionDestroyedEvent(se.getSession()));
  }
}
