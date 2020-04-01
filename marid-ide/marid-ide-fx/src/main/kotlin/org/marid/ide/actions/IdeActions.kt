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

package org.marid.ide.actions

import javafx.application.Platform
import org.marid.fx.action.Fx
import org.marid.ide.extensions.bean
import org.marid.ide.log.IdeLogWindow
import org.marid.ide.project.ProjectTabsManager
import org.marid.ide.project.Projects
import org.springframework.beans.factory.ObjectFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class IdeActions {

  @Bean
  fun exitAction() = Fx(
    text = "Exit",
    icon = "icons/close.png",
    h = { Platform.exit() },
    key = "Ctrl+Q"
  )

  @Bean
  fun newProjectAction(projects: Projects, projectTabsManager: ProjectTabsManager) = Fx(
    text = "New project",
    icon = "icons/new.png",
    h = { projectTabsManager.addProject(projects.newProject()) },
    key = "Ctrl+N"
  )

  @Bean
  fun showLogsAction(ideLogWindowFactory: ObjectFactory<IdeLogWindow>) = Fx(
    text = "Show logs",
    icon = "icons/log.png",
    h = { ideLogWindowFactory.bean.show() },
    key = "Ctrl+L"
  )
}
