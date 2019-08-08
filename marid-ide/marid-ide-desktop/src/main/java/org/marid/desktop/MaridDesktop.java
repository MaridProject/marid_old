package org.marid.desktop;

/*-
 * #%L
 * marid-ide-desktop
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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.marid.ide.IdeContext;
import org.marid.logging.MaridConsoleLogHandler;
import org.marid.logging.MaridLogFormatter;
import org.marid.spring.LoggingPostProcessor;
import org.marid.spring.init.InitBeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.logging.LogManager;
import java.util.logging.Logger;

@Configuration
@ComponentScan
public class MaridDesktop {

  public static void main(String... args) throws Exception {
    initLogging();

    try (final var context = new AnnotationConfigApplicationContext()) {
      context.setId("ide");
      context.setDisplayName("Marid IDE");
      context.setAllowCircularReferences(false);
      context.setAllowBeanDefinitionOverriding(false);
      context.getEnvironment().setDefaultProfiles("release", "desktop");
      context.getBeanFactory().addBeanPostProcessor(new LoggingPostProcessor());
      context.getBeanFactory().addBeanPostProcessor(new InitBeanPostProcessor(context));
      context.register(IdeContext.class, MaridDesktop.class);

      context.refresh();
      context.start();

      final var mainDisplay = context.getBean("mainDisplay", Display.class);
      final var mainShell = context.getBean("mainShell", Shell.class);

      while (!mainShell.isDisposed()) {
        if (!mainDisplay.readAndDispatch()) {
          mainDisplay.sleep();
        }
      }

      mainDisplay.close();
    }
  }

  private static void initLogging() throws Exception {
    LogManager.getLogManager().reset();

    try (final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties")) {
      if (is != null) {
        LogManager.getLogManager().readConfiguration(is);
      } else {
        final var logger = Logger.getLogger("");
        final var handler = new MaridConsoleLogHandler();
        handler.setFormatter(new MaridLogFormatter());
        logger.addHandler(handler);
      }
    }
  }
}
