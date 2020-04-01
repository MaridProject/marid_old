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

package org.marid.ide.project.dependencies

import javafx.collections.FXCollections
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.ContextMenuEvent
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.menuItem
import org.marid.fx.extensions.column
import org.marid.ide.extensions.bean
import org.marid.ide.project.ProjectsTable
import org.marid.ide.project.model.FxDependency
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class DependencyTable(
  projectsTable: ProjectsTable,
  private val dependencyDialogFactory: ObjectFactory<DependencyDialog>
) : TableView<FxDependency>() {

  val addDependency = Fx(
    text = "Add dependency",
    icon = "icons/add.png",
    h = { dependencyDialogFactory.bean.showAndWait().ifPresent { items.add(it) } }
  )

  val sortDependencies = Fx(
    text = "Sort dependencies",
    icon = "icons/sort.png",
    h = { items.sortWith(compareBy({ it.group.get() }, { it.artifact.get() }, { it.version.get() })) }
  )

  init {
    projectsTable.selectionModel.selectedItemProperty().addListener { _, _, n ->
      items = if (n == null) FXCollections.emptyObservableList() else n.dependencies.items
    }

    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(200, "Group") { it.group }
    column(200, "Name") { it.artifact }
    column(200, "Version") { it.version }

    rowFactory = Callback {
      TableRow<FxDependency>()
        .apply {
          val menu = ContextMenu().also { contextMenu = it }
          addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
            menu.items.clear()
            menu.items += listOf(
              addDependency.menuItem,
              SeparatorMenuItem(),
              sortDependencies.menuItem
            )
            item?.also { curItem ->
              menu.items += listOf(
                SeparatorMenuItem(),
                editFx(curItem).menuItem
              )
            }
          }
        }
    }
  }

  private fun editFx(dep: FxDependency) = Fx(
    text = "Edit...",
    icon = "icons/edit.png",
    h = { dependencyDialogFactory.bean.init(dep).showAndWait().ifPresent(dep::copyFrom) }
  )
}
