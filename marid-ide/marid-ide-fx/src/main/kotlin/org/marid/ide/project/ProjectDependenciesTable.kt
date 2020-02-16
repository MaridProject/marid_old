package org.marid.ide.project

import javafx.scene.control.TableView
import org.marid.ide.project.xml.XmlDependency
import org.springframework.stereotype.Component

@Component
class ProjectDependenciesTable : TableView<XmlDependency>() {
}