package org.marid.ide.log

import com.sun.javafx.binding.ObjectConstant
import com.sun.javafx.binding.StringConstant
import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.shape.Circle
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.marid.fx.extensions.color
import org.marid.fx.extensions.column
import org.marid.fx.extensions.formatSafe
import org.marid.fx.i18n.localized
import org.marid.ide.extensions.bean
import org.marid.spring.annotation.PrototypeScoped
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@Component
@PrototypeScoped
class IdeLogWindow(log: IdeLog, primaryStage: ObjectFactory<Stage>) : Stage(StageStyle.UTILITY) {

  private val table = TableView(log.records)
  private val pane = BorderPane(table)

  init {
    titleProperty().bind("Log".localized)
    initOwner(primaryStage.bean)
    scene = Scene(pane, primaryStage.bean.width * 0.7, primaryStage.bean.height * 0.7)

    table.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY

    table.column(40, null) {
      ObjectConstant.valueOf(Circle(8.0, it.level.color))
    }.also {
      it.graphic = ImageView(Image("icons/alarm.png", 16.0, 16.0, true, true))
      it.style = "-fx-alignment: CENTER;"
    }

    table.column(80, "Time") {
      val time = LocalTime.ofInstant(Instant.ofEpochMilli(it.millis), ZoneId.systemDefault())
      StringConstant.valueOf(time.toString())
    }.also {
      it.style = """-fx-alignment: CENTER; -fx-font-family: monospaced;"""
    }

    table.column(700, "Message") {
      StringConstant.valueOf(it.formatSafe)
    }.also {
      it.style = """-fx-font-family: monospaced;"""
    }
  }
}
