/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */

package org.marid.app.ivy;

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
