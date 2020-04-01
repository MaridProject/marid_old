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

import javafx.collections.ListChangeListener
import org.marid.ide.child.project.ProjectTab
import org.marid.ide.main.IdeTabs
import org.marid.spring.ContextUtils
import org.marid.spring.LoggingPostProcessor
import org.marid.spring.init.InitBeanPostProcessor
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class ProjectTabsManager(
  private val parentContext: GenericApplicationContext,
  private val projects: Projects,
  private val ideTabs: IdeTabs
) {

  fun addProject(project: Project) {
    if (projects.items.none { it.id == project.id }) {
      return
    }
    ideTabs.tabs.find { it.id == project.id }
      ?.also { ideTabs.selectionModel.select(it) }
      ?.also { return }
    ContextUtils.context(parentContext) { reader, ctx ->
      ctx.defaultListableBeanFactory.registerSingleton("project", project)
      ctx.defaultListableBeanFactory.addBeanPostProcessor(LoggingPostProcessor())
      ctx.defaultListableBeanFactory.addBeanPostProcessor(InitBeanPostProcessor(ctx))
      reader.register(ProjectTab::class.java)
    }.let {
      it.refresh()
      it.start()
      it.getBean(ProjectTab::class.java)
    }
  }

  @PostConstruct
  private fun initProjects() {
    projects.items.addListener(ListChangeListener { c ->
      while (c.next()) {
        if (c.wasRemoved()) {
          c.removed.forEach { r -> ideTabs.tabs.removeIf { it.id == r.id } }
        }
      }
    })
  }
}
