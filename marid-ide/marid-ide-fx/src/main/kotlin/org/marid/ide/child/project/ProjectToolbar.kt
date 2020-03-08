package org.marid.ide.child.project

import com.google.common.io.MoreFiles
import javafx.scene.control.Separator
import javafx.scene.control.ToolBar
import org.marid.fx.action.Fx
import org.marid.fx.action.toolButton
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

  @Suppress("UnstableApiUsage")
  @Init
  fun initClearCache(project: ObjectFactory<Project>, buildService: ProjectBuildService) {
    items += Separator()
    items += Fx(
      text = "Clear cache",
      icon = "icons/clean.png",
      h = {
        MoreFiles.deleteDirectoryContents(project.bean.cacheDepsDirectory)
        buildService.restart()
      },
      disabled = buildService.runningProperty()
    ).toolButton
  }
}