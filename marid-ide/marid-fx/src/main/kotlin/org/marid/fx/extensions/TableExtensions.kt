/*-
 * #%L
 * marid-fx
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.marid.fx.extensions

import com.sun.javafx.binding.StringConstant
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.button
import org.marid.fx.action.menuItem
import org.marid.fx.i18n.localized

fun <T, R> TableView<T>.column(width: Int, text: String?, value: (T) -> ObservableValue<R>) =
  column(width, text?.localized ?: StringConstant.valueOf(null), value)

fun <T, R> TableView<T>.column(width: Int, text: ObservableValue<String?>, value: (T) -> ObservableValue<R>) =
  TableColumn<T, R>()
    .apply {
      minWidth = width.toDouble() * 0.9
      prefWidth = width.toDouble()
      maxWidth = width.toDouble() * 3.0
      textProperty().bind(text)
      cellValueFactory = Callback { value(it.value) }
    }
    .also { columns += it }

fun <T, R> TreeTableView<T>.column(width: Int, text: String?, value: (T) -> ObservableValue<R>) =
  column(width, text?.localized ?: StringConstant.valueOf(null), value)

fun <T, R> TreeTableView<T>.column(width: Int, text: ObservableValue<String?>, value: (T) -> ObservableValue<R>) =
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

fun <T> TableRow<T>.installContextMenu(callback: (Int, T?) -> List<MenuItem>) {
  contextMenu = ContextMenu()
  addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
    contextMenu.items.setAll(callback(if (item == null) tableView.items.size else index, item))
  }
}

fun <T> TreeTableRow<T>.installContextMenu(callback: (TreeItem<T>?) -> List<Fx>) {
  contextMenu = ContextMenu()
  addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
    contextMenu.items.setAll(callback(treeItem).map { it.menuItem })
  }
}

fun <T> TableView<T>.placeholder(factory: () -> T?, disabled: ObservableValue<Boolean>? = null) = Fx(
  text = "Add cellar",
  icon = "icons/add.png",
  h = { factory()?.also { items.add(it) } },
  disabled = disabled
).button
