package org.marid.desktop;

/*-
 * #%L
 * marid-ide-desktop
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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
