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
package org.marid.ui.webide.base.views.main;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.marid.applib.annotation.SpringComponent;
import org.marid.applib.l10n.Strs;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.marid.ui.webide.base.views.main.MainViewManager.Project;
import org.springframework.beans.factory.ObjectFactory;

import static com.vaadin.ui.themes.ValoTheme.BUTTON_LARGE;
import static com.vaadin.ui.themes.ValoTheme.WINDOW_TOP_TOOLBAR;

@SpringComponent
public class MainToolbar extends HorizontalLayout implements Inits {

  private final MainViewManager model;
  private final MainView view;

  public MainToolbar(MainViewManager model, MainView view) {
    this.model = model;
    this.view = view;
    addStyleNames(WINDOW_TOP_TOOLBAR);
  }

  @Init
  public void initAdd(Strs strs, ObjectFactory<MainAddProjectDialog> dialogFactory) {
    final var button = new Button(VaadinIcons.FOLDER_ADD);
    button.addStyleNames(BUTTON_LARGE);
    button.setDescription(strs.s("addProject"));
    button.addClickListener(event -> getUI().addWindow(dialogFactory.getObject()));

    addComponent(button);
  }

  @Init
  public void initRemove(Strs strs) {
    final var button = new Button(VaadinIcons.FOLDER_REMOVE);
    button.addStyleNames(BUTTON_LARGE);
    button.setDescription(strs.s("removeProject"));
    button.addClickListener(event -> model.remove(view.getSelectedItems()));

    final Runnable selectionUpdater = () -> button.setVisible(!view.getSelectedItems().isEmpty());
    selectionUpdater.run();

    view.getSelectionModel().addSelectionListener(event -> selectionUpdater.run());

    addComponent(button);
  }

  @Init
  public void sepOp() {
    final var separator = new Label(" ");
    addComponent(separator);
  }

  @Init
  public void initRefresh(Strs strs) {
    final var button = new Button(VaadinIcons.REFRESH);
    button.addStyleNames(BUTTON_LARGE);
    button.addClickListener(event -> model.refresh());
    button.setDescription(strs.s("refresh"));

    addComponent(button);
  }

  @Init
  public void initLocalRefresh(Strs strs) {
    final var button = new Button(VaadinIcons.FILE_REFRESH);
    button.addStyleNames(BUTTON_LARGE);
    button.addClickListener(event -> view.getSelectedItems().forEach(Project::refresh));
    button.setDescription(strs.s("refreshItem"));

    final Runnable selectionUpdater = () -> button.setVisible(!view.getSelectedItems().isEmpty());
    selectionUpdater.run();

    view.getSelectionModel().addSelectionListener(event -> selectionUpdater.run());

    addComponent(button);
  }
}
