package org.marid.ide.main

import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import org.marid.fx.action.FxAction
import org.marid.fx.action.configure
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component

@Component
class IdeMenuBar : MenuBar() {
}

@Component("ideMenu")
class IdeMenu(menuBar: IdeMenuBar) : Menu() {
  init {
    menuBar.menus += this.configure(FxAction("IDE"))
  }
}

@Component("projectMenu")
@DependsOn("ideMenu")
class ProjectMenu(menuBar: IdeMenuBar) : Menu() {
  init {
    menuBar.menus += this.configure(FxAction("Project"))
  }
}

@Component("repositoriesMenu")
@DependsOn("projectMenu")
class RepositoriesMenu(menuBar: IdeMenuBar) : Menu() {
  init {
    menuBar.menus += this.configure(FxAction("Repositories"))
  }
}