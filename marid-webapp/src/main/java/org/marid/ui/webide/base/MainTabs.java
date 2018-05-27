/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
package org.marid.ui.webide.base;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.TabSheet;
import org.marid.applib.l10n.Strs;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.InitAfterStart;
import org.marid.applib.spring.init.Inits;
import org.marid.spring.annotation.SpringComponent;
import org.marid.ui.webide.base.views.projects.ProjectsPanel;
import org.marid.ui.webide.base.views.repositories.RepositoryForm;
import org.marid.ui.webide.base.views.session.SessionForm;

@SpringComponent
@InitAfterStart
public class MainTabs extends TabSheet implements Inits {

  public MainTabs() {
    setSizeFull();
  }

  @Init
  public void initProjects(Strs strs, ProjectsPanel projectsPanel) {
    addTab(projectsPanel, strs.s("projects"), VaadinIcons.PACKAGE);
  }

  @Init
  public void initSession(Strs strs, SessionForm sessionForm) {
    addTab(sessionForm, strs.s("session"), VaadinIcons.USER);
  }

  @Init
  public void initRepositories(Strs strs, RepositoryForm repositoryForm) {
    addTab(repositoryForm, strs.s("repositories"), VaadinIcons.LINES_LIST);
  }
}
