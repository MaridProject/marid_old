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

import com.vaadin.ui.Label;
import org.marid.applib.components.Toolbar;
import org.marid.applib.dialog.Dlg;
import org.marid.applib.spring.init.Init;
import org.marid.applib.validators.StringValidators;
import org.marid.spring.annotation.SpringComponent;

import java.util.concurrent.atomic.AtomicReference;

import static com.vaadin.icons.VaadinIcons.*;
import static org.marid.applib.utils.Locales.m;
import static org.marid.applib.utils.Locales.s;

@SpringComponent
public class ProjectToolbar extends Toolbar {

  private final ProjectManager manager;
  private final ProjectList list;

  public ProjectToolbar(ProjectManager manager, ProjectList list) {
    this.manager = manager;
    this.list = list;
  }

  @Init
  public void initAdd() {
    button(FOLDER_ADD, e -> new Dlg<>(s("addProject"), new AtomicReference<String>(), true, 350, 280)
        .addTextField(s("name"), "project", (f, b) -> b
            .asRequired(m("nameNonEmpty"))
            .withValidator(StringValidators.fileNameValidator())
            .bind(AtomicReference::get, AtomicReference::set))
        .addCancelButton(s("cancel"))
        .addSubmitButton(s("addProject"), ref -> manager.add(ref.get()))
        .show(), "addProject");
  }

  @Init
  public void initRemove() {
    final var button = button(FILE_REMOVE, e -> manager.remove(list.getSelectedItems()), "removeProject");
    final Runnable selectionUpdater = () -> button.setVisible(!list.getSelectedItems().isEmpty());
    selectionUpdater.run();
    list.addSelectionListener(event -> selectionUpdater.run());
  }

  @Init
  public void sepOp() {
    final var separator = new Label(" ");
    addComponent(separator);
  }

  @Init
  public void initRefresh() {
    button(REFRESH, e -> manager.refresh(), "refresh");
  }

  @Init
  public void initLocalRefresh() {
    final var button = button(FILE_REFRESH, e -> list.getSelectedItems().forEach(manager::refresh), "refreshItem");
    final Runnable selectionUpdater = () -> button.setVisible(!list.getSelectedItems().isEmpty());
    selectionUpdater.run();
    list.getSelectionModel().addSelectionListener(event -> selectionUpdater.run());
  }
}
