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

package org.marid.ide.child.project

import javafx.scene.control.ToolBar
import org.marid.fx.action.Fx
import org.marid.fx.action.toolButton
import org.marid.fx.extensions.deleteDirectoryContents
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.spring.init.Init
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class ProjectToolbar : ToolBar() {

  @Init
  fun initBuildButton(buildService: ProjectBuildService, project: ObjectFactory<Project>) {
    items += Fx(
      text = "Build",
      icon = "icons/build.png",
      h = { buildService.restart() },
      disabled = buildService.runningProperty().or(project.bean.locked)
    ).toolButton
  }

  @Init
  fun initRebuildButton(project: ObjectFactory<Project>, buildService: ProjectBuildService) {
    items += Fx(
      text = "Rebuild",
      icon = "icons/rebuild.png",
      h = {
        project.bean.cacheDepsDirectory.deleteDirectoryContents()
        buildService.restart()
      },
      disabled = buildService.runningProperty().or(project.bean.locked)
    ).toolButton
  }
}
