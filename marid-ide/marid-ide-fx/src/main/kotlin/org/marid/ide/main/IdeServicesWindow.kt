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

package org.marid.ide.main

import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.event.WeakEventHandler
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.cell.ProgressBarTableCell
import javafx.scene.image.ImageView
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import javafx.stage.WindowEvent
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.marid.fx.extensions.*
import org.marid.spring.annotation.PrototypeScoped
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@PrototypeScoped
@Component
class IdeServicesWindow(private val statusBar: IdeStatusBar, services: IdeServices) : Stage(StageStyle.UNDECORATED) {

  private val table = TableView(services.services)
    .apply {
      columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
      placeholder = Label().configure(Fx("No services found"))
      column(64, "Id") {
        services.id(it).readOnlyProp
      }.apply {
        style = "-fx-alignment: CENTER-LEFT;"
      }
      column(200, "Name") {
        Bindings.createStringBinding({ it.toString() }, *it.observables)
      }.apply {
        style = "-fx-alignment: CENTER-LEFT;"
      }
      column(48, "State") {
        it.stateProperty().map { service ->
          val icon = service.icon(24, 24)
          ImageView(icon)
        }
      }.apply {
        style = "-fx-alignment: CENTER;"
      }
      column(100, "Progress") {
        it.progressProperty().asDoubleProperty
      }.apply {
        cellFactory = Callback { ProgressBarTableCell() }
        style = "-fx-alignment: CENTER;"
      }
    }

  private val parentHiddenHandler = EventHandler<WindowEvent> { close() }
  private val weakParentHiddenHandler = WeakEventHandler(parentHiddenHandler)

  init {
    initOwner(statusBar.scene.window)
    scene = Scene(table, 800.0, 400.0)
    statusBar.scene.window.addEventFilter(WindowEvent.WINDOW_HIDING, weakParentHiddenHandler)
  }

  @Autowired
  fun init(context: GenericApplicationContext) {
    if (Window.getWindows().any { it is IdeServicesWindow }) {
      return
    }
    addEventFilter(WindowEvent.WINDOW_HIDDEN) { context.defaultListableBeanFactory.destroyBean(this) }
    val bounds = statusBar.localToScreen(statusBar.boundsInLocal)
    show()
    x = bounds.maxX - width
    y = bounds.minY - height
  }
}
