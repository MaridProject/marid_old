/*-
 * #%L
 * marid-test
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
package org.marid.test.logging;

import java.util.logging.LogManager;

public class TestLogInitializer {

  public TestLogInitializer() {
    LogManager.getLogManager().reset();
    LogManager.getLogManager().getLogger("").addHandler(new TestLogHandler());
  }
}
