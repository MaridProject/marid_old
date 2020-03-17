package org.marid.fx.table

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TableRow
import javafx.scene.input.ContextMenuEvent
import org.marid.fx.action.Fx
import org.marid.fx.action.menuItem
import java.util.*

class TableRowContextMenu<T>(val row: TableRow<T>) : ContextMenu() {

  private val actions = TreeMap<String, LinkedList<() -> Fx?>>()

  init {
    row.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
      items.setAll(
        actions
          .mapValues { v -> v.value.mapNotNull { it() }.map { it.menuItem } }
          .filterValues { it.isNotEmpty() }
          .toList()
          .mapIndexed { index, pair -> if (index > 0) listOf(SeparatorMenuItem()) + pair.second else pair.second }
          .flatten()
      )
    }
  }

  fun install(group: String, action: () -> Fx?) {
    actions.computeIfAbsent(group) { LinkedList() } += action
  }

  fun <R : Comparable<R>> installSort(selector: (T) -> R?) {
    install("order") {
      row.takeIf { it.tableView.items.isNotEmpty() }?.let {
        Fx(
          text = "Sort",
          icon = "icons/sort.png",
          h = { row.tableView.items.sortBy(selector) }
        )
      }
    }
  }

  fun installAdd(dialog: Boolean, itemCreator: () -> T?) {
    install("modify") {
      row.takeUnless { it.isEmpty }?.let {
        val action: (ActionEvent) -> Unit = { itemCreator()?.also { row.tableView.items.add(row.index, it) } }
        Fx(
          text = if (dialog) "Add..." else "Add",
          icon = "icons/add.png",
          h = { ev -> if (dialog) Platform.runLater { action(ev) } else action(ev) }
        )
      }
    }
    install("modify") {
      val action: (ActionEvent) -> Unit = { itemCreator()?.also { row.tableView.items.add(it) } }
      Fx(
        text = if (dialog) "Append..." else "Append",
        icon = "icons/append.png",
        h = { ev -> if (dialog) Platform.runLater { action(ev) } else action(ev) }
      )
    }
  }

  fun installDelete() {
    install("modify") {
      row.takeUnless { it.isEmpty }?.let {
        Fx(
          text = "Remove",
          icon = "icons/delete.png",
          h = { row.tableView.items.removeAt(row.index) }
        )
      }
    }
  }
}