package org.marid.ide.log

import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.stage.PopupWindow
import org.marid.spring.annotation.PrototypeScoped
import org.springframework.stereotype.Component
import java.util.logging.LogRecord

@Component
@PrototypeScoped
class IdeLogWindow(log: IdeLog) : PopupWindow() {

  private val table = TableView<LogRecord>(log.records)
  private val pane = BorderPane(table)

  init {
    scene = Scene(pane, 800.0, 600.0)
  }
}