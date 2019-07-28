package org.marid.profile.event;

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

import org.marid.profile.IdeProfile;
import org.marid.project.IdeProject;
import org.marid.spring.events.PropagatedEvent;

public class IdeProfileOnAddProjectEvent extends PropagatedEvent<IdeProfile> {

  private final IdeProject project;

  public IdeProfileOnAddProjectEvent(IdeProfile source, IdeProject project) {
    super(source);
    this.project = project;
  }

  public IdeProject getProject() {
    return project;
  }
}