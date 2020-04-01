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

import javafx.scene.control.TabPane
import org.marid.ide.common.IdePreferences
import org.marid.ide.project.ProjectsTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class IdeTabs(idePreferences: IdePreferences) : TabPane() {
  init {
    sideProperty().bind(idePreferences.primaryTabsSide)
  }
}

@Component
class IdeTabsCollector(private val ideTabs: IdeTabs) {

  @Autowired
  fun initTabs(projectsTab: ProjectsTab) {
    ideTabs.tabs += listOf(projectsTab)
  }
}
