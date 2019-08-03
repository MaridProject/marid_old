/*-
 * #%L
 * marid-ide-model
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

package org.marid.project.ivy.infrastructure;

import static org.apache.ivy.util.Message.*;

public class IvyLoggerAdapter implements BaseMessageLogger {

  private final System.Logger log;

  public IvyLoggerAdapter(System.Logger log) {
    this.log = log;
  }

  private System.Logger.Level level(int level) {
    switch (level) {
      case MSG_INFO: return System.Logger.Level.INFO;
      case MSG_DEBUG: return System.Logger.Level.DEBUG;
      case MSG_ERR: return System.Logger.Level.ERROR;
      case MSG_WARN: return System.Logger.Level.WARNING;
      default: return System.Logger.Level.TRACE;
    }
  }

  @Override
  public void log(String msg, int level) {
    log.log(level(level), msg);
  }
}
