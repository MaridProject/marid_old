package org.marid.ide.main

import javafx.beans.property.Property
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.marid.fx.action.Fx
import org.marid.fx.extensions.icon
import org.marid.fx.extensions.item
import org.marid.fx.extensions.menu
import org.marid.fx.i18n.localized
import org.marid.ide.common.IdePreferences
import org.marid.spring.init.Init
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class IdeMenuBar : MenuBar() {

  val ideMenu = menu("IDE")
  val projectMenu = menu("Project")
  val repositoriesMenu = menu("Repositories")
  val preferencesMenu = menu("Preferences")

  @Bean fun ideMenuItems() = IdeMenuItems()
  @Bean fun projectMenuItems() = ProjectMenuItems()
  @Bean fun preferencesMenuItems(idePreferences: IdePreferences) = PreferencesMenuItems(idePreferences)

  inner class IdeMenuItems {

    lateinit var exit: MenuItem

    @Init fun exit(exitAction: Fx) = ideMenu.item(exitAction).also { exit = it }
  }

  inner class ProjectMenuItems {

    lateinit var newProject: MenuItem

    @Init fun newProject(newProjectAction: Fx) = projectMenu.item(newProjectAction).also { newProject = it }
  }

  inner class PreferencesMenuItems(idePreferences: IdePreferences) {

    val tabsMenu = preferencesMenu.menu(Fx("Tabs", "icons/tabs.png"))
    val primarySideMenu = tabsMenu.menu(Fx("Primary tabs side", "icons/primary-tabs.png"))
    val secondarySideMenu = tabsMenu.menu(Fx("Secondary tabs side", "icons/secondary-tabs.png"))

    init {
      apply(idePreferences.primaryTabsSide, primarySideMenu)
      apply(idePreferences.secondaryTabsSide, secondarySideMenu)
    }

    private fun apply(property: Property<Side>, menu: Menu) {
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
  }
}