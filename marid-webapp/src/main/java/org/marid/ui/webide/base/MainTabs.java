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
import org.marid.applib.components.ToolbarForm;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.InitAfterStart;
import org.marid.spring.annotation.SpringComponent;
import org.marid.ui.webide.base.views.artifacts.ArtifactPanel;
import org.marid.ui.webide.base.views.projects.ProjectsPanel;
import org.marid.ui.webide.base.views.repositories.RepositoryPanel;
import org.marid.ui.webide.base.views.session.SessionForm;
import org.marid.ui.webide.base.views.session.SessionToolbar;

import static org.marid.applib.utils.Locales.s;

@SpringComponent
@InitAfterStart
public class MainTabs extends TabSheet {

  public MainTabs() {
    setSizeFull();
  }

  @Init
  public void initProjects(ProjectsPanel projectsPanel) {
    addTab(projectsPanel, s("projects"), VaadinIcons.PACKAGE);
  }

  @Init
  public void initSession(SessionToolbar toolbar, SessionForm sessionForm) {
    addTab(new ToolbarForm<>(toolbar, sessionForm), s("session"), VaadinIcons.USER);
  }

  @Init
  public void initRepositories(RepositoryPanel repositoryPanel, ArtifactPanel artifactPanel) {
    final var tabs = new TabSheet();
    tabs.setSizeFull();
    tabs.addTab(repositoryPanel, s("repositories"), VaadinIcons.LINES);
    tabs.addTab(artifactPanel, s("artifacts"), VaadinIcons.LINES_LIST);
    addTab(tabs, s("libraries"), VaadinIcons.BOOK);
  }
}
