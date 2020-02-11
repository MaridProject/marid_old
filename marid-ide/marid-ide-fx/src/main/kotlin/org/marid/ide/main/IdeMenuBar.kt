package org.marid.ide.main

import javafx.beans.property.Property
import javafx.geometry.Side
import javafx.scene.control.*
import org.marid.fx.action.Fx
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

    val tabsMenu = preferencesMenu.menu("Tabs")
    val primarySideMenu = tabsMenu.menu("Primary side")
    val alternativeSideMenu = tabsMenu.menu("Alternative side")

    init {
      apply(idePreferences.primaryTabsSide, primarySideMenu)
      apply(idePreferences.alternateTabsSide, alternativeSideMenu)
    }

    private fun apply(property: Property<Side>, menu: Menu) {
      val group = ToggleGroup()
      Side.values().forEach { side ->
        menu.items += RadioMenuItem()
          .also { it.textProperty().bind(side.name.localized) }
          .also { it.toggleGroup = group }
          .also { if (side == property.value) it.isSelected = true }
          .also { it.selectedProperty().addListener { _, _, v -> if (v) property.value = side } }
      }
    }
  }
}