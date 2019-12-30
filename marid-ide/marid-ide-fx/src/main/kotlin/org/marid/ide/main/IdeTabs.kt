package org.marid.ide.main

import javafx.collections.ListChangeListener
import javafx.scene.control.TabPane
import org.marid.ide.child.project.ProjectTab
import org.marid.ide.project.Projects
import org.marid.ide.project.Project
import org.marid.spring.ContextUtils
import org.marid.spring.LoggingPostProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@Component
class IdeTabs : TabPane() {

  @Autowired private fun initProjects(projects: Projects, parent: GenericApplicationContext) {
    projects.items.addListener(ListChangeListener { c ->
      while (c.next()) {
        if (c.wasUpdated() || c.wasPermutated()) {
          continue
        }
        if (c.wasRemoved()) {
          tabs.removeIf { tab -> c.removed.any { it.id == tab.id } }
        }
        if (c.wasAdded()) {
          c.addedSubList.forEach { tabs += addProject(it, parent) }
        }
      }
    })
  }

  private fun addProject(project: Project, parent: GenericApplicationContext) =
    ContextUtils.context(parent) { reader, ctx ->
      ctx.defaultListableBeanFactory.registerSingleton("project", project)
      ctx.defaultListableBeanFactory.addBeanPostProcessor(LoggingPostProcessor())
      reader.register(ProjectTab::class.java)
    }.let {
      it.refresh()
      it.start()
      it.getBean(ProjectTab::class.java)
    }
}