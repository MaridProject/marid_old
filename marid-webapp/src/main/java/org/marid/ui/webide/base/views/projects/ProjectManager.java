/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.ui.webide.base.views.projects;

import org.marid.applib.manager.ListManager;
import org.marid.ui.webide.base.dao.ProjectDao;
import org.marid.ui.webide.base.model.ProjectItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectManager extends ListManager<ProjectDao, ProjectItem> {

  public ProjectManager(ProjectDao dao) {
    super(dao);
  }

  @Override
  public void add(List<ProjectItem> added) {
    final var addedListener = addAddListener(e -> e.update.values().forEach(dao::create));
    try {
      super.add(added);
    } finally {
      removeAddListener(addedListener);
    }
  }

  @Override
  public void remove(int... indices) {
    final var removedListener = addRemoveListener(e -> e.update.values().forEach(dao::removeProject));
    try {
      super.remove(indices);
    } finally {
      removeRemoveListener(removedListener);
    }
  }

  @Override
  public void remove(List<ProjectItem> removed) {
    final var removedListener = addRemoveListener(e -> e.update.values().forEach(dao::removeProject));
    try {
      super.remove(removed);
    } finally {
      removeRemoveListener(removedListener);
    }
  }
}
