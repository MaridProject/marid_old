package org.marid.fx.table

import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TableRow
import javafx.scene.input.ContextMenuEvent
import org.marid.fx.action.Fx
import org.marid.fx.action.menuItem
import org.marid.fx.extensions.toBottom
import org.marid.fx.extensions.toTop
import org.marid.fx.extensions.up
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

  fun installReorder() {
    install("order") {
      row.takeIf { it.index > 0 }?.let {
        Fx(
          text = "To top",
          icon = "icons/top.png",
          h = { row.tableView.items.toTop(row.index) }
        )
      }
    }
    install("order") {
      row.takeIf { it.index < it.tableView.items.size - 1 }?.let {
        Fx(
          text = "To bottom",
          icon = "icons/bottom.png",
          h = { row.tableView.items.toBottom(row.index) }
        )
      }
    }
    install("order") {
      row.takeIf { it.index < it.tableView.items.size - 1 }?.let {
        Fx(
          text = "Up",
          icon = "icons/up.png",
          h = { row.tableView.items.up(row.index) }
        )
      }
    }
  }

  fun installAdd(itemCreator: () -> T?) {
    install("modify") {
      row.takeUnless { it.isEmpty }?.let {
        Fx(
          text = "Add...",
          icon = "icons/add.png",
          h = { itemCreator()?.also { row.tableView.items.add(row.index, it) } }
        )
      }
    }
    install("modify") {
      Fx(
        text = "Append...",
        icon = "icons/append.png",
        h = { itemCreator()?.also { row.tableView.items.add(it) } }
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