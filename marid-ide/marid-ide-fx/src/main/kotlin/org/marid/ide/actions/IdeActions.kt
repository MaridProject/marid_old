package org.marid.ide.actions

import javafx.application.Platform
import org.marid.fx.action.FxAction
import org.marid.ide.main.IdeTabs
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
  fun newProjectAction(projects: Projects, tabs: IdeTabs): FxAction = FxAction(
    text = "New project",
    icon = "icons/new.png",
    handler = {tabs.addProject(projects.newProject())},
    key = "Ctrl+N"
  )
}