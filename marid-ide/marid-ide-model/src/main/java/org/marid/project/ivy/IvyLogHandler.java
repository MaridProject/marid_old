package org.marid.project.ivy;

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

import org.marid.project.IdeProject;
import org.marid.project.ivy.event.IvyLogEvent;
import org.marid.project.ivy.event.IvyProgressEvent;
import org.marid.project.ivy.infrastructure.BaseMessageLogger;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import static org.apache.ivy.util.Message.*;

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
      case MSG_INFO:
        return System.Logger.Level.INFO;
      case MSG_ERR:
        return System.Logger.Level.ERROR;
      case MSG_DEBUG:
        return System.Logger.Level.DEBUG;
      case MSG_VERBOSE:
        return System.Logger.Level.TRACE;
      case MSG_WARN:
        return System.Logger.Level.WARNING;
      default:
        return System.Logger.Level.OFF;
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
