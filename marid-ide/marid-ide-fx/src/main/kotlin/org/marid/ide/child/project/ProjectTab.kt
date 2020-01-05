package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.image.ImageView
import org.marid.ide.main.IdeTabs
import org.marid.ide.project.Project
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@Component
@ComponentScan
class ProjectTab(projectContent: ProjectContent, project: ObjectFactory<Project>) : Tab(null, projectContent) {

  private val project = project.`object`

  init {
    id = this.project.id
    textProperty().bind(this.project.name)
    graphic = ImageView("icons/project.png")
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