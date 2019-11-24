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

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Dmitry Ovchinnikov
 */
public class MaridLauncher {

  public static void main(String... args) throws Throwable {
    if (args.length == 0) {
      System.out.println("Usage: java -jar <marid-runtime-jar-file> <url-of-deployment-jar> [<deployment arguments>]");
      return;
    }

    try (final var deployment = new WineryRuntime(new URL(args[0]), List.of(Arrays.copyOfRange(args, 1, args.length)))) {
      deployment.start();

      final var scanner = new Scanner(System.in);
      while (scanner.hasNextLine()) {
        final var cmd = scanner.nextLine().trim();
        switch (cmd) {
          case "q":
          case "quit":
            deployment.close();
            break;
          case "h":
          case "halt":

            break;
        }
      }
    }
  }
}
