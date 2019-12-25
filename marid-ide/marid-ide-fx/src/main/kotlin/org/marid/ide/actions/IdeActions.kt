package org.marid.ide.actions

import javafx.application.Platform
import org.marid.fx.action.FxAction
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
}