package org.marid.ide.child.project

import javafx.scene.layout.BorderPane
import org.marid.ide.project.Projects
import org.marid.spring.beans.InternalBean
import org.springframework.stereotype.Component

@Component
class ProjectContent(project: InternalBean<Projects.Project>) : BorderPane() {
  private val project = project.bean
}