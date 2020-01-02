package org.marid.ide.main

import javafx.collections.ListChangeListener
import javafx.scene.control.TabPane
import org.marid.ide.child.project.ProjectTab
import org.marid.ide.project.Project
import org.marid.ide.project.Projects
import org.marid.spring.ContextUtils
import org.marid.spring.LoggingPostProcessor
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class IdeTabs(
  private val parentContext: GenericApplicationContext,
  private val projects: Projects
) : TabPane() {

  fun addProject(project: Project) {
    if (projects.items.none { it.id == project.id }) {
      return
    }
    tabs.find { it.id == project.id }
      ?.also { selectionModel.select(it) }
      ?.also { return }
    ContextUtils.context(parentContext) { reader, ctx ->
      ctx.defaultListableBeanFactory.registerSingleton("project", project)
      ctx.defaultListableBeanFactory.addBeanPostProcessor(LoggingPostProcessor())
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
          c.removed.forEach { r -> tabs.removeIf { it.id == r.id } }
        }
      }
    })
  }
}