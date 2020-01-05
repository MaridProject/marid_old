package org.marid.ide.project

import javafx.collections.ListChangeListener
import org.marid.ide.child.project.ProjectTab
import org.marid.ide.main.IdeTabs
import org.marid.spring.ContextUtils
import org.marid.spring.LoggingPostProcessor
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