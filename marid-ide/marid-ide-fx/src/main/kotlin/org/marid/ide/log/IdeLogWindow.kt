/*-
 * #%L
 * marid-ide-fx
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
