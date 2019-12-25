package org.marid.ide.main

import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import org.marid.fx.action.FxAction
import org.marid.fx.extensions.item
import org.marid.fx.extensions.menu
import org.marid.spring.init.Init
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class IdeMenuBar : MenuBar() {

  val ideMenu = menu("IDE")
  val projectMenu = menu("Project")
  val repositoriesMenu = menu("Repositories")

  @Bean
  fun ideMenuItems() = IdeMenuItems()

  inner class IdeMenuItems {

    @Autowired private lateinit var exitAction: FxAction

    lateinit var exit: MenuItem

    @Init
    fun exit() {
      exit = ideMenu.item(exitAction)
    }
  }
}