package org.marid.ide.main

import javafx.scene.control.MenuBar
import org.marid.fx.menu.FxMenu
import org.springframework.stereotype.Component

@Component
class IdeMenuBar : MenuBar() {

  val ide = FxMenu("IDE").also { menus += it }
  val project = FxMenu("Project").also { menus += it }
  val repositories = FxMenu("Repositories").also { menus += it }
}