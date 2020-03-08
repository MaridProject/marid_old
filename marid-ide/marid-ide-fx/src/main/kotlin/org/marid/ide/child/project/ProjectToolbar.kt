package org.marid.ide.child.project

import javafx.scene.control.ToolBar
import org.marid.fx.action.Fx
import org.marid.fx.action.toolButton
import org.marid.spring.init.Init
import org.springframework.stereotype.Component

@Component
class ProjectToolbar : ToolBar() {

  @Init
  fun initBuildButton(buildService: ProjectBuildService) {
    items += Fx(
      text = "Build",
      icon = "icons/build.png",
      h = { buildService.restart() },
      disabled = buildService.runningProperty()
    ).toolButton
  }
}