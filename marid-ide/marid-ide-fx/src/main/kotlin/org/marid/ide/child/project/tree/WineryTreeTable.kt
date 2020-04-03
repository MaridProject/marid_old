/*-
 * #%L
 * marid-ide-fx
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

package org.marid.ide.child.project.tree

import javafx.scene.control.*
import javafx.util.Callback
import org.marid.fx.action.Fx
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
import org.marid.ide.project.model.FxRack
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
                CONSTANTS -> {
                  constants(ti, list)
                }
                RACKS -> {
                  racks(ti, list)
                }
              }
            }
            is CellarConstantItem -> {
              Fx("Insert", "icons/insert.png").menu.also {
                list += it
                constants(ti, it.items, ti.parent.children.indexOf(ti))
              }
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
          Tid.from(c, c.declaringClass.simpleName, "icons/rack.png")
            .fx {
              treeItem.ancestor(CellarItem::class)
                ?.also { cellarItem ->
                  (0..Short.MAX_VALUE).asSequence()
                    .map { if (it == 0) c.declaringClass.simpleName else c.declaringClass.simpleName + it }
                    .find { name -> cellarItem.value.entity.racks.none { it.getName() == name } }
                    ?.also { name ->
                      cellarItem.value.entity.racks.addOrAppend(index, FxRack()
                        .apply { setName(name) }
                        .apply { setFactory(c.declaringClass.name) }
                      )
                      treeItem.isExpanded = true
                    }
                }
            }
            .menuItem.also { pi.items += it }
        }
      }
  }
}
