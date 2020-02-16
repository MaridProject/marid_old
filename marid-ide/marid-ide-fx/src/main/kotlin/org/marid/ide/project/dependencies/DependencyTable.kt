package org.marid.ide.project.dependencies

import javafx.scene.control.TableView
import org.marid.ide.project.xml.XmlDependency
import org.springframework.stereotype.Component

@Component
class DependencyTable : TableView<XmlDependency>() {
}