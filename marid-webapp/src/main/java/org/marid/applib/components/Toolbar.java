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

import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.jetbrains.annotations.PropertyKey;
import org.marid.l10n.L10n;

public class Toolbar extends HorizontalLayout {

  protected Button button(Resource icon,
                          Button.ClickListener clickListener,
                          @PropertyKey(resourceBundle = "res.strings") String description,
                          Object... params) {
    final var session = VaadinSession.getCurrent();
    final var locale = session.getLocale();
    final var button = new Button(icon, clickListener);

    button.addStyleName(ValoTheme.BUTTON_LARGE);
    button.setDescription(L10n.s(locale, description, params));

    addComponent(button);

    return button;
  }
}
