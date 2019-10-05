package org.marid.project.ivy;

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

import org.marid.project.IdeProject;
import org.marid.project.ivy.event.IvyLogEvent;
import org.marid.project.ivy.event.IvyProgressEvent;
import org.marid.project.ivy.infrastructure.BaseMessageLogger;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import static org.apache.ivy.util.Message.MSG_DEBUG;
import static org.apache.ivy.util.Message.MSG_ERR;
import static org.apache.ivy.util.Message.MSG_INFO;
import static org.apache.ivy.util.Message.MSG_VERBOSE;
import static org.apache.ivy.util.Message.MSG_WARN;

@Component
public class IvyLogHandler implements BaseMessageLogger {

  private final GenericApplicationContext context;
  private final IdeProject project;

  public IvyLogHandler(GenericApplicationContext context, IdeProject project) {
    this.context = context;
    this.project = project;
  }

  @Override
  public void log(String msg, int level) {
    context.publishEvent(new IvyLogEvent(context, project, level(level), msg));
  }

  private System.Logger.Level level(int level) {
    switch (level) {
      case MSG_INFO: return System.Logger.Level.INFO;
      case MSG_ERR: return System.Logger.Level.ERROR;
      case MSG_DEBUG: return System.Logger.Level.DEBUG;
      case MSG_VERBOSE: return System.Logger.Level.TRACE;
      case MSG_WARN: return System.Logger.Level.WARNING;
      default: return System.Logger.Level.OFF;
    }
  }

  @Override
  public boolean isShowProgress() {
    return true;
  }

  @Override
  public void progress() {
    context.publishEvent(new IvyProgressEvent(context, project, true, ""));
  }

  @Override
  public void endProgress() {
    context.publishEvent(new IvyProgressEvent(context, project, false, ""));
  }

  @Override
  public void endProgress(String msg) {
    context.publishEvent(new IvyProgressEvent(context, project, false, msg));
  }
}
