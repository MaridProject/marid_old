package org.marid.ide.child.project.tree

import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import org.springframework.stereotype.Component

@Component
class WineryTreeTable(data: TreeData) : TreeTableView<Item<*>>(data.root) {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    columns += TreeTableColumn<Item<*>, String>().apply {
      minWidth = 150.0
      prefWidth = 200.0
      maxWidth = 600.0

    }
  }
}