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
import com.vaadin.ui.VerticalLayout;
import org.marid.applib.l10n.Strs;
import org.marid.ui.webide.base.MainTabs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainPanel extends VerticalLayout {

  public MainPanel(MainToolbar toolbar, MainView view) {
    setSizeFull();
    addComponent(toolbar);
    addComponentsAndExpand(view);
  }

  @Autowired
  private void initTab(MainTabs tabs, Strs strs) {
    tabs.addTab(this, strs.s("projects"), VaadinIcons.PACKAGE);
  }
}
