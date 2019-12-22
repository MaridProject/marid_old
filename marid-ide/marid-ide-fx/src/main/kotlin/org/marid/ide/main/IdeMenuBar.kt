package org.marid.ide.main

import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import org.marid.fx.extensions.menu
import org.marid.spring.init.Init
import org.springframework.stereotype.Component

@Component
class IdeMenuBar : MenuBar() {

  val ideMenu = menu("IDE")
  val projectMenu = menu("Project")
  val repositoriesMenu = menu("Repositories")

  @Component
  class IdeMenuItems {

    lateinit var exit: MenuItem

    @Init
    fun exit() {
      println(1)
    }
  }
}