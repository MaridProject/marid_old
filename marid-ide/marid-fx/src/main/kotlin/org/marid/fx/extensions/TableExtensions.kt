package org.marid.fx.extensions

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.util.Callback
import org.marid.fx.i18n.localized

fun <T, R> TableView<T>.column(width: Int, text: String, value: (T) -> ObservableValue<R>) =
  column(width, text.localized, value)

fun <T, R> TableView<T>.column(width: Int, text: ObservableValue<String>, value: (T) -> ObservableValue<R>) =
  TableColumn<T, R>()
    .apply {
      minWidth = width.toDouble() * 0.9
      prefWidth = width.toDouble()
      maxWidth = width.toDouble() * 3.0
      textProperty().bind(text)
      cellValueFactory = Callback { value(it.value) }
    }
    .also { columns += it }

fun <T, R> TreeTableView<T>.column(width: Int, text: String, value: (T) -> ObservableValue<R>) =
  column(width, text.localized, value)

fun <T, R> TreeTableView<T>.column(width: Int, text: ObservableValue<String>, value: (T) -> ObservableValue<R>) =
  TreeTableColumn<T, R>()
    .apply {
      minWidth = width.toDouble() * 0.9
      prefWidth = width.toDouble()
      maxWidth = width.toDouble() * 3.0
      textProperty().bind(text)
      cellValueFactory = Callback { value(it.value.value) }
    }
    .also { columns += it }

fun <T> TableView<T>.installEdit(handler: (ObservableList<T>) -> Unit) {
  addEventHandler(KeyEvent.KEY_PRESSED) {
    if ((it.code == KeyCode.ENTER || it.code == KeyCode.F2) && !it.isAltered) {
      if (selectionModel.selectedItems.isNotEmpty()) {
        handler(selectionModel.selectedItems)
      }
    }
  }
  addEventHandler(MouseEvent.MOUSE_CLICKED) {
    if (it.clickCount == 2 && !it.isAltered) {
      if (selectionModel.selectedItems.isNotEmpty()) {
        handler(selectionModel.selectedItems)
      }
    }
  }
}