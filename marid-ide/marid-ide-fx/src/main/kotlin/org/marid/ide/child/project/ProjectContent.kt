package org.marid.ide.child.project

import javafx.scene.layout.BorderPane
import org.marid.ide.project.Project
import org.marid.spring.beans.InternalBean
import org.springframework.stereotype.Component

@Component
class ProjectContent(project: InternalBean<Project>) : BorderPane() {
  private val project = project.bean
}