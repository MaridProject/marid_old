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

import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.util.Callback
import org.marid.fx.dialog.FxDialog
import org.marid.fx.extensions.bindSize
import org.marid.fx.extensions.column
import org.marid.fx.extensions.placeholder
import org.marid.fx.extensions.value
import org.marid.fx.table.TableRowContextMenu
import org.marid.ide.project.dialogs.CellarDialogData
import org.marid.ide.project.model.FxCellar
import org.springframework.stereotype.Component

@Component
class CellarsTable(private val projectsTable: ProjectsTable) : TableView<FxCellar>() {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(300, "Name") { it.name }
    column(100, "Racks") { it.racks.bindSize }
    column(100, "Constants") { it.constants.bindSize }

    placeholder = placeholder({ createCellar() }, projectsTable.project.isNull)

    rowFactory = Callback {
      TableRow<FxCellar>().apply {
        contextMenu = TableRowContextMenu(this).also { menu ->
          menu.installSort { it.getName() }
          menu.installAdd { createCellar() }
          menu.installDelete()
          menu.installReorder()
        }
      }
    }

    itemsProperty().bind(projectsTable.cellars)
  }

  private fun createCellar() = FxDialog(CellarDialogData(projectsTable.selectionModel.selectedItem.winery, null))
    .also { it.dialogPane.setPrefSize(400.0, 300.0) }
    .value(CellarDialogData::invoke)
}
