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

package org.marid.ide.project.repositories

import javafx.collections.FXCollections
import javafx.scene.control.TableView
import org.marid.fx.extensions.column
import org.marid.ide.project.ProjectsTable
import org.marid.ide.project.model.FxRepository
import org.springframework.stereotype.Component

@Component
class ProjectRepositoriesTable(
  projectsTable: ProjectsTable
) : TableView<FxRepository>() {

  init {
    projectsTable.selectionModel.selectedItemProperty().addListener { _, _, n ->
      items = if (n == null) FXCollections.emptyObservableList() else n.repositories.items
    }

    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(200, "Name") { it.name }
    column(200, "URL") { it.url }
  }
}
