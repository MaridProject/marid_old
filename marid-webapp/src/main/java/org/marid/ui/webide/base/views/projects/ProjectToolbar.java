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
package org.marid.ui.webide.base.views.projects;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.marid.applib.utils.ToolbarSupport;
import org.marid.spring.annotation.SpringComponent;
import org.springframework.beans.factory.ObjectFactory;

import static com.vaadin.icons.VaadinIcons.*;
import static com.vaadin.ui.themes.ValoTheme.WINDOW_TOP_TOOLBAR;

@SpringComponent
public class ProjectToolbar extends HorizontalLayout implements Inits, ToolbarSupport {

  private final ProjectManager manager;
  private final ProjectList list;

  public ProjectToolbar(ProjectManager manager, ProjectList list) {
    this.manager = manager;
    this.list = list;
    addStyleNames(WINDOW_TOP_TOOLBAR);
  }

  @Init
  public void initAdd(ObjectFactory<AddProjectDialog> dialogFactory) {
    addComponent(button(FOLDER_ADD, e -> getUI().addWindow(dialogFactory.getObject()), "addProject"));
  }

  @Init
  public void initRemove() {
    final var button = button(FILE_REMOVE, e -> manager.remove(list.getSelectedItems()), "removeProject");
    final Runnable selectionUpdater = () -> button.setVisible(!list.getSelectedItems().isEmpty());
    selectionUpdater.run();
    list.addSelectionListener(event -> selectionUpdater.run());
    addComponent(button);
  }

  @Init
  public void sepOp() {
    final var separator = new Label(" ");
    addComponent(separator);
  }

  @Init
  public void initRefresh() {
    addComponent(button(REFRESH, e -> manager.refresh(), "refresh"));
  }

  @Init
  public void initLocalRefresh() {
    final var button = button(FILE_REFRESH, e -> list.getSelectedItems().forEach(manager::refresh), "refreshItem");
    final Runnable selectionUpdater = () -> button.setVisible(!list.getSelectedItems().isEmpty());
    selectionUpdater.run();
    list.getSelectionModel().addSelectionListener(event -> selectionUpdater.run());
    addComponent(button);
  }
}
