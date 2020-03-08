package org.marid.ide.child.project

import javafx.scene.layout.BorderPane
import org.marid.ide.child.project.tree.WineryTreeTable
import org.springframework.stereotype.Component

@Component
class ProjectChildPane(
  tree: WineryTreeTable,
  toolbar: ProjectToolbar
) : BorderPane(tree, toolbar, null, null, null) {
}