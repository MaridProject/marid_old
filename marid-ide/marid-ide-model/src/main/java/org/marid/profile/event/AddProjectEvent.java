package org.marid.profile.event;

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

import org.marid.profile.IdeProfile;
import org.marid.project.IdeProject;
import org.marid.spring.events.PropagatedEvent;

public class AddProjectEvent extends PropagatedEvent<IdeProfile> {

  private final IdeProject project;

  public AddProjectEvent(IdeProfile source, IdeProject project) {
    super(source);
    this.project = project;
  }

  public IdeProject getProject() {
    return project;
  }
}