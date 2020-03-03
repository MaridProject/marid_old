package org.marid.ide.child.project.tree

import javafx.scene.control.TreeTableRow
import javafx.scene.control.TreeTableView
import javafx.util.Callback
import org.marid.fx.extensions.bindString
import org.marid.fx.extensions.column
import org.marid.fx.extensions.installContextMenu
import org.marid.ide.child.project.model.Item
import org.marid.ide.child.project.model.TreeData
import org.springframework.stereotype.Component

@Component
class WineryTreeTable(data: TreeData) : TreeTableView<Item<*>>(data.root) {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
    isShowRoot = false

    column(200, "Name") { it.name }
    column(200, "Factory") { it.factory }
    column(300, "Value") { it.value }
    column(300, "Type") { it.resolvedType.bindString { t -> if (t.value == Void.TYPE) "" else t.value.toString() } }

    rowFactory = Callback {
      TreeTableRow<Item<*>>().apply {
        installContextMenu {
          listOf(
          )
        }
      }
    }
  }
}