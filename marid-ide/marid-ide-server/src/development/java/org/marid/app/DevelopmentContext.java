package org.marid.app;

/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class DevelopmentContext {

  @Bean(initMethod = "start")
  public static Thread quitter(GenericApplicationContext context) {
    final Thread thread = new Thread(null, () -> {
      try (final Scanner scanner = new Scanner(System.in)) {
        while (scanner.hasNextLine()) {
          switch (scanner.nextLine().trim()) {
            case "q":
            case "quit":
              context.close();
              System.exit(0);
              break;
          }
        }
      }
    }, "quitter", 64L * 1024L, false);
    thread.setDaemon(true);
    return thread;
  }
}
