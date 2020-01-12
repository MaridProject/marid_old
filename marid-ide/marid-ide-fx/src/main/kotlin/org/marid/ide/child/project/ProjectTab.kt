package org.marid.ide.child.project

import javafx.scene.control.Tab
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
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
class ProjectTab(tabs: ProjectTabs, project: ObjectFactory<Project>) : Tab(null, tabs) {

  private val project = project.bean

  init {
    id = this.project.id
    configure(Fx(icon = "icons/project.png").text(this.project.winery.name))
    isClosable = true
  }

  @Autowired
  private fun initContext(context: GenericApplicationContext) {
    tabPaneProperty().addListener { _, _, n ->
      if (n == null) {
        context.close()
      }
    }
  }

  @Autowired
  private fun initTabs(tabs: IdeTabs) {
    tabs.tabs += this
    tabs.selectionModel.select(this)
  }
}