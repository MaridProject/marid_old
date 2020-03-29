package org.marid.ide.child.project.tree

import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableRow
import javafx.scene.control.TreeTableView
import javafx.util.Callback
import org.marid.fx.action.menu
import org.marid.fx.action.menuItem
import org.marid.fx.extensions.addOrAppend
import org.marid.fx.extensions.bindString
import org.marid.fx.extensions.column
import org.marid.fx.extensions.installContextMenu
import org.marid.ide.child.project.ProjectScanner
import org.marid.ide.child.project.model.*
import org.marid.ide.child.project.model.SubItem.Kind.CONSTANTS
import org.marid.ide.child.project.model.SubItem.Kind.RACKS
import org.marid.ide.project.model.FxCellarConstant
import org.marid.idelib.Tid
import org.springframework.stereotype.Component

@Component
class WineryTreeTable(data: TreeData, private val projectScanner: ProjectScanner) : TreeTableView<Item<*>>(data.root) {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
    isShowRoot = false

    column(200, "Name") { it.name }
    column(200, "Factory") { it.factory }
    column(300, "Value") { it.value }
    column(300, "Type") { it.resolvedType.bindString { t -> if (t.value == Void.TYPE) "" else t.value.toString() } }

    rowFactory = Callback {
      TreeTableRow<Item<*>>().apply {
        installContextMenu { ti ->
          val list = mutableListOf<MenuItem>()
          when (val v = ti?.value) {
            is SubItem -> {
              when (v.kind) {
                CONSTANTS -> constants(ti, list)
                RACKS -> racks(ti, list)
              }
            }
            is CellarConstantItem -> {
              constants(ti, list, ti.parent.children.indexOf(ti))
            }
          }
          list
        }
      }
    }
  }

  private fun constants(treeItem: TreeItem<Item<*>>, list: MutableList<MenuItem>, index: Int = -1) {
    projectScanner.constants()
      .groupBy { it.declaringClass.`package` }
      .mapValues { (_, v) -> v.groupBy { it.declaringClass } }
      .forEach { (p, pels) ->
        val pi = Tid.from(p, p.name, "icons/pkg.png").fx.menu.also { list += it }
        pels.forEach { (c, cels) ->
          val ci = Tid.from(c, c.simpleName, "icons/class.png").fx.menu.also { pi.items += it }
          cels.forEach { m ->
            Tid.from(m, m.name, "icons/const.png")
              .fx {
                treeItem.ancestor(CellarItem::class)
                  ?.also { cellarItem ->
                    (0..Short.MAX_VALUE).asSequence()
                      .map { if (it == 0) m.name else m.name + it }
                      .find { name -> cellarItem.value.entity.constants.none { it.getName() == name } }
                      ?.also { name ->
                        cellarItem.value.entity.constants.addOrAppend(index, FxCellarConstant()
                          .apply { setName(name) }
                          .apply { setFactory(c.name) }
                          .apply { setSelector(m.name) }
                        )
                        treeItem.isExpanded = true
                      }
                  }
              }
              .menuItem.also { ci.items += it }
          }
        }
      }
  }

  private fun racks(treeItem: TreeItem<Item<*>>, list: MutableList<MenuItem>, index: Int = -1) {
    projectScanner.racks()
      .groupBy { it.declaringClass.`package` }
      .forEach { (p, pels) ->
        val pi = Tid.from(p, p.name, "icons/pkg.png").fx.menu.also { list += it }
        pels.forEach { c ->
          Tid.from(c, c.declaringClass.simpleName, "icons/const.png")
            .fx {

            }
            .menuItem.also { pi.items += it }
        }
      }
  }
}