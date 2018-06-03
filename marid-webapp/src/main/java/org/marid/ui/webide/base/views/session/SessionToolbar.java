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
package org.marid.ui.webide.base.views.session;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.themes.ValoTheme;
import org.marid.applib.components.Toolbar;
import org.marid.applib.spring.init.Init;
import org.marid.spring.annotation.SpringComponent;

@SpringComponent
public class SessionToolbar extends Toolbar {

  public SessionToolbar() {
    addStyleName(ValoTheme.WINDOW_TOP_TOOLBAR);
  }

  @Init
  public void initClose(VaadinSession session) {
    button(VaadinIcons.CLOSE, e -> {
      session.close();
      getUI().getPage().setLocation("/app/logout");
    }, "exitSession");
  }
}
