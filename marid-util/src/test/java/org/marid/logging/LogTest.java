/*-
 * #%L
 * marid-util
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
package org.marid.logging;

import org.marid.test.TestGroups;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.marid.logging.Log.log;
import static org.testng.Assert.assertEquals;

public class LogTest extends AbstractHandler {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private final AtomicInteger counter = new AtomicInteger();

  @BeforeTest(groups = {TestGroups.NORMAL})
  public void init() {
    logger.setUseParentHandlers(false);
    logger.addHandler(this);
    logger.setLevel(Level.INFO);
  }

  @Test(groups = {TestGroups.NORMAL})
  public void testLog() {
    log(Level.INFO, "message");
    assertEquals(counter.get(), 1);
  }

  @Override
  public void publish(LogRecord record) {
    counter.incrementAndGet();
  }
}
