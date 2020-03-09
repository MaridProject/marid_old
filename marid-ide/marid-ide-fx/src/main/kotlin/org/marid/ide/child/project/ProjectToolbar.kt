package org.marid.ide.child.project

import javafx.scene.control.ToolBar
import org.marid.fx.action.Fx
import org.marid.fx.action.toolButton
import org.marid.fx.extensions.deleteDirectoryContents
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.spring.init.Init
import org.springframework.beans.factory.ObjectFactory
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

  @Init
  fun initRebuild(project: ObjectFactory<Project>, buildService: ProjectBuildService) {
    items += Fx(
      text = "Rebuild",
      icon = "icons/rebuild.png",
      h = {
        project.bean.cacheDepsDirectory.deleteDirectoryContents()
        buildService.restart()
      },
      disabled = buildService.runningProperty()
    ).toolButton
  }
}