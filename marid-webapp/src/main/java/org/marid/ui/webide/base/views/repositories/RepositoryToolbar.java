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
package org.marid.ui.webide.base.views.repositories;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import org.marid.applib.components.Toolbar;
import org.marid.applib.dialog.Dialog;
import org.marid.applib.spring.init.Init;
import org.marid.applib.validators.StringValidators;
import org.marid.spring.annotation.SpringComponent;
import org.marid.ui.webide.base.dao.RepositoriesDao;
import org.marid.ui.webide.base.model.RepositoryItem;

import static com.vaadin.icons.VaadinIcons.FILE_ADD;
import static com.vaadin.icons.VaadinIcons.FILE_REMOVE;
import static org.marid.applib.utils.Locales.m;
import static org.marid.applib.utils.Locales.s;

@SpringComponent
public class RepositoryToolbar extends Toolbar {

  private final RepositoryList list;

  public RepositoryToolbar(RepositoryList list) {
    this.list = list;
  }

  @Init
  public void initAdd(RepositoryManager manager, RepositoriesDao dao) {
    button(FILE_ADD, e -> new Dialog<>(s("addProject"), new RepositoryItem(), 400, 300)
        .addTextField(s("name"), "repository", (f, b) -> b
            .withValidator(StringValidators.fileNameValidator())
            .withValidator(manager::isNew, c -> m("alreadyExists"))
            .bind(RepositoryItem::getName, RepositoryItem::setName))
        .add(() -> {
          final var map = dao.selectorsMap();
          final var combo = new ComboBox<>(s("repository"), map.keySet());
          combo.setTextInputAllowed(false);
          combo.setItemCaptionGenerator(k -> k + " (" + map.get(k) + ")");
          combo.setEmptySelectionAllowed(false);
          map.descendingKeySet().stream().findFirst().ifPresent(combo::setSelectedItem);
          return combo;
        }, (f, b) -> b.bind(RepositoryItem::getSelector, RepositoryItem::setSelector))
        .addCancelButton(s("cancel"))
        .addSubmitButton(s("addProject"), manager::add)
        .show(), "addRepository");
  }

  @Init
  public void initRemove(RepositoryManager manager) {
    final var button = button(FILE_REMOVE, e -> list.getSelectedItems().forEach(manager::remove), "removeItem");
    final Runnable selectionUpdater = () -> button.setVisible(!list.getSelectedItems().isEmpty());
    selectionUpdater.run();
    list.addSelectionListener(event -> selectionUpdater.run());
  }

  @Init
  public void sepOp() {
    final var separator = new Label(" ");
    addComponent(separator);
  }

  @SuppressWarnings("deprecation")
  @Init
  public void initSave(RepositoryManager manager) {
    button(FontAwesome.SAVE, e -> manager.save(), "save");
  }
}
