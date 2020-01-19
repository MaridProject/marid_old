package org.marid.fx.extensions

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
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