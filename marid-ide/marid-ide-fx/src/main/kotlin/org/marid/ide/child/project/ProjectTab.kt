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

import javafx.scene.control.Tab
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.marid.fx.extensions.mapObject
import org.marid.ide.extensions.bean
import org.marid.ide.main.IdeTabs
import org.marid.ide.project.Project
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@Component
@ComponentScan
class ProjectTab(
  pane: ProjectChildPane,
  project: ObjectFactory<Project>,
  buildService: ProjectBuildService
) : Tab(null, pane) {

  private val project = project.bean

  init {
    id = this.project.id
    textProperty().bind(this.project.winery.name)
    graphicProperty().bind(buildService.dirty.mapObject {
      val icon = if (it) "icons/modified-project.png" else "icons/unmodified-project.png"
      ImageView(Image(icon, 18.0, 18.0, true, true))
    })
    isClosable = true
  }

  @Autowired
  private fun initContext(context: GenericApplicationContext) {
    tabPaneProperty().addListener { _, _, n -> n ?: context.close() }
  }

  @Autowired
  private fun initTabs(tabs: IdeTabs) {
    tabs.tabs += this
    tabs.selectionModel.select(this)
  }
}
