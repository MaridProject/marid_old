package org.marid.ide.child.project

import javafx.scene.layout.BorderPane
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class ProjectContent(
  project: ObjectFactory<Project>,
  tabs: ProjectTabTabs
) : BorderPane(tabs) {
  private val project = project.bean
}