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
package org.marid.applib.spring.init;

import org.marid.cache.MaridClassValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

class InitUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(InitUtils.class);

  static final MaridClassValue<Map<String, Integer>> METHOD_ORDERS = new MaridClassValue<>(c -> () -> {
    final var name = c.getSimpleName() + "_MethodOrder.properties";

    try (final InputStream inputStream = c.getResourceAsStream(name)) {

      if (inputStream == null) {
        LOGGER.warn("Unable to find {}", name);
        return Collections.emptyMap();
      }

      final HashMap<String, Integer> map = new HashMap<>();
      try (final Scanner scanner = new Scanner(inputStream, UTF_8)) {
        for (int i = 0; scanner.hasNextLine(); i++) {
          final var line = scanner.nextLine();
          if (line.isEmpty()) {
            continue;
          }
          map.put(line.trim(), i);
        }
      }
      return map;
    }
  });
}
