package org.marid.ide.project

import javafx.scene.control.TableView
import org.marid.ide.project.xml.XmlRepository
import org.springframework.stereotype.Component

@Component
class ProjectRepositoriesTable : TableView<XmlRepository>() {
}