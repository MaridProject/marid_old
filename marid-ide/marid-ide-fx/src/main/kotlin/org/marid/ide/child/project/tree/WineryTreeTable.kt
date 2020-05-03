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

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableRow
import javafx.scene.control.TreeTableView
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.menuItem
import org.marid.fx.extensions.column
import org.marid.fx.extensions.installContextMenu
import org.marid.fx.extensions.mapString
import org.marid.ide.child.project.actions.ItemActions
import org.marid.ide.child.project.model.*
import org.marid.ide.child.project.model.SubItem.Kind.CONSTANTS
import org.marid.ide.child.project.model.SubItem.Kind.RACKS
import org.springframework.stereotype.Component

@Component
class WineryTreeTable(data: TreeData, actions: ItemActions) : TreeTableView<Item<*>>(data.root) {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
    isShowRoot = false

    column(200, "Name") { it.name }
    column(200, "Factory") { it.factory }
    column(300, "Value") { it.value }
    column(300, "Type") { it.resolvedType.mapString { t -> if (t == Void.TYPE) "" else t.toString() } }

    root.addEventHandler(TreeItem.treeNotificationEvent<Item<*>>()) {
      it.treeItem.isExpanded = true
    }

    rowFactory = Callback {
      TreeTableRow<Item<*>>().apply {
        installContextMenu { ti ->
          when (val v = ti?.value) {
            is SubItem -> {
              when (v.kind) {
                CONSTANTS -> actions.constantActions(ti.ancestor(CellarItem::class)!!.value.entity, -1)
                RACKS -> actions.rackActions(ti.ancestor(CellarItem::class)!!.value.entity, -1)
                else -> listOf()
              }
            }
            is CellarConstantItem -> {
              listOf(
                Fx("Insert", "icons/insert.png").also {
                  it.children(
                    actions.constantActions(
                      ti.ancestor(CellarItem::class)!!.value.entity, ti.parent.children.indexOf(ti)
                    )
                  )
                })
            }
            else -> listOf()
          }.map { it.menuItem }
        }
      }
    }
  }
}
