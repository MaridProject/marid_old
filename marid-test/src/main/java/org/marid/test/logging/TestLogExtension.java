package org.marid.test.logging;

/*-
 * #%L
 * marid-test
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

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TestLogExtension implements BeforeAllCallback {

  @Override
  public void beforeAll(ExtensionContext context) {
    final var logger = Logger.getLogger("");

    if (Arrays.stream(logger.getHandlers()).noneMatch(h -> h instanceof TestLogHandler)) {
      LogManager.getLogManager().reset();
      logger.addHandler(new TestLogHandler());
    }
  }
}
