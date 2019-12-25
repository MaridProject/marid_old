package org.marid.ide.actions

import javafx.application.Platform
import org.marid.fx.action.FxAction
import org.marid.ide.project.Project
import org.marid.ide.project.Projects
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class IdeActions {

  @Bean
  fun exitAction(): FxAction = FxAction(
    text = "Exit",
    icon = "icons/close.png",
    handler = { Platform.exit() },
    key = "Ctrl+Q"
  )

  @Bean
  fun newProjectAction(projects: Projects): FxAction = FxAction(
    text = "New project",
    icon = "icons/new.png",
    handler = {projects.items.add(Project())},
    key = "Ctrl+N"
  )
}