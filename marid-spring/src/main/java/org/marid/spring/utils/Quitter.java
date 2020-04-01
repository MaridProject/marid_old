package org.marid.spring.utils;

/*-
 * #%L
 * marid-spring
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Quitter extends Thread implements InitializingBean {

  private final GenericApplicationContext context;

  public Quitter(GenericApplicationContext context) {
    super(null, null, "Quitter", 64L * 1024L, false);
    setDaemon(true);
    setPriority(Thread.MIN_PRIORITY);
    this.context = context;
  }

  @Override
  public void run() {
    final var console = System.console();
    if (console != null) {
      while (true) {
        final var line = console.readLine();
        if (line == null) {
          break;
        }
        process(line);
      }
      return;
    }

    final Scanner scanner;
    try {
      final var channel = System.inheritedChannel();
      if (channel instanceof ReadableByteChannel) {
        scanner = new Scanner((ReadableByteChannel) channel, StandardCharsets.UTF_8);
      } else {
        scanner = new Scanner(System.in);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    while (scanner.hasNextLine()) {
      process(scanner.nextLine());
    }
  }

  private void process(String line) {
    switch (line.trim()) {
      case "q":
      case "quit":
      case "exit":
        context.close();
        break;
    }
  }

  @Override
  public void afterPropertiesSet() {
    start();
  }
}
