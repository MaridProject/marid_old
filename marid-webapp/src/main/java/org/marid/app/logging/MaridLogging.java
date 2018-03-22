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

package org.marid.app.logging;

import org.marid.logging.MaridLogManager;

import java.io.InputStream;
import java.util.logging.LogManager;

public class MaridLogging {

  public static void initLogging() throws Exception {
    System.setProperty("java.util.logging.manager", MaridLogManager.class.getName());

    final LogManager logManager = LogManager.getLogManager();
    try (final InputStream inputStream = MaridLogging.class.getResourceAsStream("/logging.properties")) {
      logManager.readConfiguration(inputStream);
    }
  }
}
