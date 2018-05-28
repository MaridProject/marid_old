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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.marid.spring.annotation.SpringComponent;

import static com.vaadin.icons.VaadinIcons.*;
import static com.vaadin.ui.themes.ValoTheme.WINDOW_TOP_TOOLBAR;
import static org.marid.applib.utils.ToolbarSupport.button;

@SpringComponent
public class RepositoryToolbar extends HorizontalLayout implements Inits {

  private final RepositoryList list;

  public RepositoryToolbar(RepositoryList list) {
    this.list = list;
    addStyleName(WINDOW_TOP_TOOLBAR);
  }

  @Init
  public void initAdd() {
    addComponent(button(FILE_ADD, e -> {
    }, "addRepository"));
  }

  @Init
  public void initRemove() {
    final var button = button(FILE_REMOVE, e -> list.getDataProvider(), "removeItem");
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
  public void initSave() {
    addComponent(button(REFRESH, e -> {}, "refresh"));
  }
}
