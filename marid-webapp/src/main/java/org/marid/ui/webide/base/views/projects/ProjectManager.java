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

import org.marid.ui.webide.base.dao.ProjectDao;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class ProjectManager {

  private final ProjectDao dao;
  private final ArrayList<String> projects = new ArrayList<>();

  public ProjectManager(ProjectDao dao) {
    this.dao = dao;
  }

  @PostConstruct
  public void refresh() {
    projects.clear();
    projects.addAll(dao.getProjectNames());
  }

  public void remove(Collection<String> projects) {
    projects.forEach(dao::removeProject);
    refresh();
  }

  public void add(String project) {
    dao.tryCreate(project);
    refresh();
  }
}
