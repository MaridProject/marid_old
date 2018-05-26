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
import org.marid.applib.annotation.SpringComponent;
import org.marid.applib.l10n.Strs;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;

@SpringComponent
public class MainToolbar extends HorizontalLayout implements Inits {

  private final MainViewModel model;
  private final MainView view;

  public MainToolbar(MainViewModel model, MainView view) {
    this.model = model;
    this.view = view;
  }

  @Init
  public void initAdd(Strs strs) {
    final var button = new Button(VaadinIcons.FOLDER_ADD);
    button.setDescription(strs.s("addProject"));

    addComponent(button);
  }

  @Init
  public void initRemove(Strs strs) {
    final var button = new Button(VaadinIcons.FOLDER_REMOVE);
    button.setDescription(strs.s("removeProject"));

    addComponent(button);
  }
}
