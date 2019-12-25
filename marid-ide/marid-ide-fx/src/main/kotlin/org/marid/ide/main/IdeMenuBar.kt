package org.marid.ide.main

import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import org.marid.fx.action.FxAction
import org.marid.fx.extensions.item
import org.marid.fx.extensions.menu
import org.marid.spring.init.Init
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class IdeMenuBar : MenuBar() {

  val ideMenu = menu("IDE")
  val projectMenu = menu("Project")
  val repositoriesMenu = menu("Repositories")

  @Bean fun ideMenuItems() = IdeMenuItems()
  @Bean fun projectMenuItems() = ProjectMenuItems()

  inner class IdeMenuItems {

    lateinit var exit: MenuItem

    @Init fun exit(exitAction: FxAction) = ideMenu.item(exitAction).also { exit = it }
  }

  inner class ProjectMenuItems {

    lateinit var newProject: MenuItem

    @Init fun newProject(newProjectAction: FxAction) = projectMenu.item(newProjectAction).also { newProject = it }
  }
}