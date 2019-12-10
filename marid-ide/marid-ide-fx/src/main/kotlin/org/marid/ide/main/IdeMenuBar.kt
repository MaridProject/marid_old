package org.marid.ide.main

import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import org.springframework.stereotype.Component

@Component
class IdeMenuBar : MenuBar() {

  val ide = Menu("IDE").also { menus += it }
  val project = Menu("Project").also { menus += it }
  val repositories = Menu("Repositories").also { menus += it }
}