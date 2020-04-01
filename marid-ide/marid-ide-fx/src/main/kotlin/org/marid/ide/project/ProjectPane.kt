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

import javafx.scene.control.Accordion
import javafx.scene.control.TitledPane
import org.marid.fx.i18n.localized
import org.marid.ide.project.dependencies.DependencyTable
import org.marid.ide.project.repositories.ProjectRepositoriesTable
import org.springframework.stereotype.Component

@Component
class ProjectPane(
  cellarsTable: CellarsTable,
  dependencyTable: DependencyTable,
  projectRepositoriesTable: ProjectRepositoriesTable,
  projectManagementPane: ProjectManagementPane
) : Accordion(
  TitledPane(null, cellarsTable).also {
    it.textProperty().bind("Cellars".localized)
  },
  TitledPane(null, dependencyTable).also {
    it.textProperty().bind("Dependencies".localized)
  },
  TitledPane(null, projectRepositoriesTable).also {
    it.textProperty().bind("Repositories".localized)
  },
  TitledPane(null, projectManagementPane).also {
    it.textProperty().bind("Management".localized)
  }
) {
  init {
    expandedPane = panes[0]
  }
}
