package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.image.ImageView
import org.marid.ide.project.Projects.Project
import org.marid.spring.beans.InternalBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@Component
@ComponentScan
class ProjectTab(projectContent: ProjectContent, project: InternalBean<Project>) : Tab(null, projectContent) {

  private val project = project.bean

  init {
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
}