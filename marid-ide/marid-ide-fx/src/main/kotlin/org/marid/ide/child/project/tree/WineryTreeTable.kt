package org.marid.ide.child.project.tree

import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeTableRow
import javafx.scene.control.TreeTableView
import javafx.util.Callback
import org.marid.fx.extensions.bindString
import org.marid.fx.extensions.column
import org.marid.fx.extensions.installContextMenu
import org.marid.fx.i18n.i18n
import org.marid.ide.child.project.ProjectScanner
import org.marid.ide.child.project.model.Item
import org.marid.ide.child.project.model.SubItem
import org.marid.ide.child.project.model.SubItem.Kind.CONSTANTS
import org.marid.ide.child.project.model.SubItem.Kind.RACKS
import org.marid.ide.child.project.model.TreeData
import org.marid.misc.Annotations.fetchOne
import org.marid.runtime.annotation.Title
import org.springframework.stereotype.Component

@Component
class WineryTreeTable(data: TreeData, projectScanner: ProjectScanner) : TreeTableView<Item<*>>(data.root) {

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
                CONSTANTS -> {
                  val grouped = projectScanner.constants()
                    .groupBy { it.declaringClass.`package` }
                    .mapValues { (_, v) -> v.groupBy { it.declaringClass } }
                  grouped.forEach { (p, pels) ->
                    val pt = fetchOne(p, Title::class.java).map { it.value }.orElseGet { p.name }
                    val pi = Menu(pt.i18n()).also { list += it }
                    pels.forEach { (c, cels) ->
                      val ct = fetchOne(c, Title::class.java).map { it.value }.orElseGet { c.simpleName }
                      val ci = Menu(ct.i18n()).also { pi.items += it }
                      cels.forEach { m ->
                        val cot = fetchOne(m, Title::class.java).map { it.value }.orElseGet { m.name }
                        val coi = MenuItem(cot).also { ci.items += it }
                      }
                    }
                  }
                }
                RACKS -> {

                }
              }
            }
          }
          list
        }
      }
    }
  }
}