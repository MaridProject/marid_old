package org.marid.ide.actions

import javafx.application.Platform
import org.marid.fx.action.Fx
import org.marid.ide.project.ProjectTabsManager
import org.marid.ide.project.Projects
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class IdeActions {

  @Bean
  fun exitAction(): Fx = Fx(
    text = "Exit",
    icon = "icons/close.png",
    handler = { Platform.exit() },
    key = "Ctrl+Q"
  )

  @Bean
  fun newProjectAction(projects: Projects, projectTabsManager: ProjectTabsManager): Fx = Fx(
    text = "New project",
    icon = "icons/new.png",
    handler = { projectTabsManager.addProject(projects.newProject()) },
    key = "Ctrl+N"
  )
}