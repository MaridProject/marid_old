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
