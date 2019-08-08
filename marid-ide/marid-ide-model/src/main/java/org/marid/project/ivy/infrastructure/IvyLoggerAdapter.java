/*-
 * #%L
 * marid-ide-model
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
