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
package org.marid.ui.webide.base.views;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.marid.applib.l10n.Strs;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Initializers;
import org.marid.applib.view.StaticView;
import org.marid.applib.view.ViewName;
import org.springframework.stereotype.Component;

@ViewName("")
@Component
public class MainView extends VerticalLayout implements StaticView, Initializers {

  private final MenuBar menuBar = new MenuBar();

  public MainView() {
    addComponent(menuBar);
    setSizeFull();
    menuBar.setWidth(100, Unit.PERCENTAGE);
  }

  @Init(1)
  public void initSession(Strs strs) {
    final var sessions = menuBar.addItem(strs.s("session"));
    sessions.setEnabled(true);

    {
      final var logout = sessions.addItem(strs.s("logout"), item -> {
        getSession().close();
        getUI().getPage().setLocation("/app");
      });
      logout.setEnabled(true);
    }
  }
}
