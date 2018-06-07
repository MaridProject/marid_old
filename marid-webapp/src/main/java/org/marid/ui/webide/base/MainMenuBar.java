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
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.marid.applib.spring.init.Init;
import org.marid.spring.annotation.SpringComponent;
import org.marid.ui.webide.base.views.session.SessionForm;
import org.springframework.beans.factory.ObjectFactory;

import static org.marid.applib.utils.Locales.s;

@SpringComponent
public class MainMenuBar extends MenuBar {

  private final MenuItem sessionItem;

  public MainMenuBar() {
    sessionItem = addItem(s("session"), VaadinIcons.USER, null);
    setWidth(100, Unit.PERCENTAGE);
    setHeight(-1, Unit.PIXELS);
  }

  @Init
  public void sessionInfo(ObjectFactory<SessionForm> sessionFormFactory) {
    sessionItem.addItem(s("information"), VaadinIcons.INFO_CIRCLE, item -> {
      final Window window = new Window(s("sessionInformation"), sessionFormFactory.getObject());
      window.setModal(true);
      window.setResizable(false);
      window.setWindowMode(WindowMode.MAXIMIZED);
      UI.getCurrent().addWindow(window);
    });
  }

  @Init
  public void sepBeforeLogout() {
    sessionItem.addSeparator();
  }

  @Init
  public void logout() {
    sessionItem.addItem(s("logout"), VaadinIcons.EXIT, item -> {
      final var ui = getUI();
      final var session = ui.getSession();
      ui.getPage().replaceState("/logout");
      session.close();
    });
  }

  public MenuItem getSessionItem() {
    return sessionItem;
  }
}
