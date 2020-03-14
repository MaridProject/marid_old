package org.marid.ide.actions

import javafx.application.Platform
import org.marid.fx.action.Fx
import org.marid.ide.extensions.bean
import org.marid.ide.log.IdeLogWindow
import org.marid.ide.project.ProjectTabsManager
import org.marid.ide.project.Projects
import org.springframework.beans.factory.ObjectFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class IdeActions {

  @Bean
  fun exitAction() = Fx(
    text = "Exit",
    icon = "icons/close.png",
    h = { Platform.exit() },
    key = "Ctrl+Q"
  )

  @Bean
  fun newProjectAction(projects: Projects, projectTabsManager: ProjectTabsManager) = Fx(
    text = "New project",
    icon = "icons/new.png",
    h = { projectTabsManager.addProject(projects.newProject()) },
    key = "Ctrl+N"
  )

  @Bean
  fun showLogsAction(ideLogWindowFactory: ObjectFactory<IdeLogWindow>) = Fx(
    text = "Show logs",
    icon = "icons/log.png",
    h = { ideLogWindowFactory.bean.show() },
    key = "Ctrl+L"
  )
}