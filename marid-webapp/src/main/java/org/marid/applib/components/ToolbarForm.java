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
package org.marid.applib.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class ToolbarForm<T extends Toolbar, C extends Component> extends VerticalLayout {

  private final T toolbar;
  private final C component;

  public ToolbarForm(T toolbar, C component) {
    setSpacing(true);
    setMargin(true);
    addComponent(this.toolbar = toolbar);
    addComponentsAndExpand(this.component = component);
  }

  public T getToolbar() {
    return toolbar;
  }

  public C getComponent() {
    return component;
  }
}
