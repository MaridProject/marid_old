/*-
 * #%L
 * marid-ide-fx
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

package org.marid.ide.main

import javafx.beans.property.Property
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.marid.fx.action.Fx
import org.marid.fx.extensions.checkItem
import org.marid.fx.extensions.icon
import org.marid.fx.extensions.item
import org.marid.fx.extensions.menu
import org.marid.fx.i18n.localized
import org.marid.ide.common.IdePreferences
import org.marid.spring.init.Init
import org.springframework.stereotype.Component

@Component
class IdeMenuBar : MenuBar() {

  // IDE menu
  val ideMenu = menu("IDE")

  @Init fun exit(exitAction: Fx) = ideMenu.item(exitAction)

  val projectMenu = menu("Project")

  @Init fun newProject(newProjectAction: Fx) = projectMenu.item(newProjectAction)

  val preferencesMenu = menu("Preferences")
  val tabsMenu = preferencesMenu.menu(Fx("Tabs", "icons/tabs.png"))
  val primarySideMenu = tabsMenu.menu(Fx("Primary tabs side", "icons/primary-tabs.png"))
  val secondarySideMenu = tabsMenu.menu(Fx("Secondary tabs side", "icons/secondary-tabs.png"))

  init {
    preferencesMenu.items.add(SeparatorMenuItem())
  }

  @Init fun initPrefs(idePreferences: IdePreferences) {
    preferencesMenu.checkItem(Fx("Show types", "icons/type.png", selected = idePreferences.showTypes))

    fun apply(property: Property<Side>, menu: Menu) {
      val group = ToggleGroup()
      Side.values().forEach { side ->
        menu.items += RadioMenuItem()
          .also { it.textProperty().bind(side.name.localized) }
          .also { it.graphic = ImageView(Image(side.icon, 20.0, 20.0, true, true)) }
          .also { it.toggleGroup = group }
          .also { if (side == property.value) it.isSelected = true }
          .also { it.selectedProperty().addListener { _, _, v -> if (v) property.value = side } }
      }
    }

    apply(idePreferences.primaryTabsSide, primarySideMenu)
    apply(idePreferences.secondaryTabsSide, secondarySideMenu)
  }

  val servicesMenu = menu("Services")

  val logMenu = menu("Logs")

  @Init fun showLogsMenuItem(showLogsAction: Fx) = logMenu.item(showLogsAction)
}
