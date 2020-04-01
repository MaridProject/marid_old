/*-
 * #%L
 * marid-fx
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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

package org.marid.fx.extensions

import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.marid.fx.i18n.localized

fun MenuBar.menu(text: String): Menu = Menu()
  .also {
    it.textProperty().bind(text.localized)
    menus += it
  }

fun Menu.menu(text: String): Menu = Menu()
  .also {
    it.textProperty().bind(text.localized)
    items += it
  }

fun Menu.menu(action: Fx): Menu = Menu()
  .also {
    it.configure(action)
    items += it
  }

fun Menu.item(action: Fx): MenuItem = MenuItem()
  .also {
    it.configure(action)
    items += it
  }

fun Menu.checkItem(action: Fx): CheckMenuItem = CheckMenuItem()
  .also {
    it.configure(action)
    items += it
  }
