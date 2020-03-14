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