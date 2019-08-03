package org.marid.project.ivy.event;

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
import org.marid.spring.events.BroadcastEvent;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.System.Logger.Level;

public class IvyLogEvent extends BroadcastEvent<IdeProject> {

  public final Level level;
  public final String message;

  public IvyLogEvent(GenericApplicationContext context, IdeProject source, Level level, String message) {
    super(context, source);
    this.level = level;
    this.message = message;
  }
}
