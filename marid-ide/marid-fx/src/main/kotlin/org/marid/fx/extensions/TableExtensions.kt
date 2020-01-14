package org.marid.fx.extensions

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.TableColumn.CellDataFeatures
import javafx.scene.control.TableView
import javafx.util.Callback
import org.marid.fx.i18n.localized

typealias ColumnCellValue<T, R> = (CellDataFeatures<T, R>) -> ObservableValue<R>

fun <T, R> TableView<T>.column(width: Int, text: String, value: ColumnCellValue<T, R>) = TableColumn<T, R>()
  .apply {
    minWidth = width.toDouble() * 0.9
    prefWidth = width.toDouble()
    maxWidth = width.toDouble() * 3.0
    textProperty().bind(text.localized)
    cellValueFactory = Callback { value(it) }
  }
  .also { columns += it }