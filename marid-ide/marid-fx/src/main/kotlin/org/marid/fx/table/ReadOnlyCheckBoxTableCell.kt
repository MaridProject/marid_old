package org.marid.fx.table

import javafx.scene.control.CheckBox
import javafx.scene.control.TableCell

class ReadOnlyCheckBoxTableCell<S> : TableCell<S, Boolean?>() {

  private val checkBox = object : CheckBox() {
    override fun arm() {}
  }

  init {
    styleClass.add("check-box-table-cell")
    graphic = checkBox
    text = null
  }

  override fun updateItem(item: Boolean?, empty: Boolean) {
    super.updateItem(item, empty)
    if (empty || item == null) {
      checkBox.isVisible = false
    } else {
      graphic = checkBox
      checkBox.isVisible = true
      checkBox.isSelected = item
    }
  }
}