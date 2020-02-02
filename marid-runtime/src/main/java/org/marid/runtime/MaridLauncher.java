/*-
 * #%L
 * marid-runtime
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

package org.marid.runtime;

import org.marid.runtime.internal.WineryRuntime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import static java.lang.System.in;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.copyOfRange;

/**
 * @author Dmitry Ovchinnikov
 */
public class MaridLauncher {

  public static void main(String... args) throws Throwable {
    if (args.length == 0) {
      System.out.println("Usage: java -jar <marid-runtime-jar-file> <url-of-deployment-jar> [<deployment arguments>]");
      return;
    }
    final var reader = new BufferedReader(new InputStreamReader(in, UTF_8));
    try (final var deployment = new WineryRuntime(new URL(args[0]), List.of(copyOfRange(args, 1, args.length)))) {
      deployment.start();
      final var destroyer = new Thread(null, () -> run(reader, deployment), "Marid", 64L << 10, false);
      destroyer.setDaemon(true);
      destroyer.start();
    }
  }

  private static void run(BufferedReader reader, WineryRuntime runtime) {
    while (true) {
      final String line;
      try {
        line = reader.readLine();
      } catch (Throwable e) {
        e.printStackTrace();
        break;
      }
      if (line == null) {
        break;
      }
      final var cmd = line.trim();
      switch (cmd) {
        case "q":
        case "quit":
          try {
            runtime.close();
          } catch (Throwable e) {
            e.printStackTrace();
          }
          break;
        case "h":
        case "halt":
          System.exit(1);
          break;
      }
    }
  }
}
