/*-
 * #%L
 * marid-ide-server
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

package org.marid.app.ivy;

import org.marid.logging.Log;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.ivy.util.Message.*;

public class IvyLoggerAdapter implements BaseMessageLogger {

  private final Logger logger;

  public IvyLoggerAdapter(Logger logger) {
    this.logger = logger;
  }

  private Level level(int level) {
    switch (level) {
      case MSG_INFO: return Level.INFO;
      case MSG_DEBUG: return Level.CONFIG;
      case MSG_ERR: return Level.SEVERE;
      case MSG_WARN: return Level.WARNING;
      case MSG_VERBOSE: return Level.FINE;
      default: return Level.FINER;
    }
  }

  @Override
  public void log(String msg, int level) {
    Log.log(logger, level(level), msg);
  }
}
