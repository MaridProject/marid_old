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

package org.marid.ide.project

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Region
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.button
import org.marid.fx.action.menuItem
import org.marid.fx.action.toolButton
import org.marid.fx.extensions.column
import org.marid.fx.extensions.installEdit
import org.marid.fx.extensions.map
import org.marid.fx.extensions.readOnlyProp
import org.marid.ide.project.model.FxCellar
import org.springframework.stereotype.Component

@Component
class ProjectsTable(
  projects: Projects,
  newProjectAction: Fx,
  private val manager: ProjectTabsManager
) : TableView<Project>(projects.items) {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
    placeholder = newProjectAction.button
    rowFactory = Callback {
      TableRow<Project>().apply {
        contextMenu = ContextMenu()
        addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
          contextMenu.items.clear()
          item?.apply { contextMenu.items += menuItems }
        }
      }
    }
    column(100, "Id") { it.id.readOnlyProp }.also {
      it.style = "-fx-alignment: CENTER; -fx-font-family: monospaced"
    }
    column(300, "Name") { it.winery.name }.also {
      it.style = "-fx-alignment: CENTER-LEFT;"
    }
    column(150, "Group") { it.winery.group }.also {
      it.style = "-fx-alignment: CENTER;"
    }
    column(80, "Version") { it.winery.version }.also {
      it.style = "-fx-alignment: CENTER;"
    }
    column(150, "Actions") {
      val buttons = it.actions.map(Fx::toolButton).toTypedArray()
      SimpleObjectProperty(
        FlowPane(3.0, 0.0, *buttons).apply {
          prefWrapLength = Double.MAX_VALUE
          alignment = Pos.BASELINE_CENTER
          prefHeight = Region.USE_PREF_SIZE
        }
      )
    }
    installEdit { items ->
      items.forEach { manager.addProject(it) }
    }
  }

  val project: ReadOnlyObjectProperty<Project?> get() = selectionModel.selectedItemProperty()
  val cellars: ObservableValue<ObservableList<FxCellar>>
    get() = project.map { it?.winery?.cellars ?: FXCollections.emptyObservableList() }

  private val Project.menuItems
    get() = listOf(
      listOf(
        Fx(icon = "icons/delete.png", text = "Delete", h = { delete() }).menuItem
      ),
      actions.map { it.menuItem }
    ).reduce { l1, l2 -> l1 + listOf(SeparatorMenuItem()) + l2 }

  private val Project.actions
    get() = listOf(
      Fx(icon = "icons/open.png", text = "Open", h = { manager.addProject(this) }),
      Fx(icon = "icons/save.png", text = "Save", h = { save() })
    )
}
