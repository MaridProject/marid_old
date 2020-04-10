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

package org.marid.ide.child.project.model

import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.Event.fireEvent
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeItem.TreeModificationEvent
import org.marid.fx.extensions.FX_CLEANER
import org.marid.fx.extensions.identityMap
import org.marid.ide.child.project.model.SubItem.Kind.*
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.ide.project.model.*
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.lang.ref.WeakReference

@Component
class TreeData(projectFactory: ObjectFactory<Project>) {

  val root = projectFactory.bean.winery.let {
    TreeItem<Item<*>>(WineryItem(it)).apply {
      link(this, it.cellars, ::item)
    }
  }

  private companion object {

    private fun item(cellar: FxCellar) = TreeItem<Item<*>>(CellarItem(cellar))
      .apply { children += TreeItem<Item<*>>(SubItem(RACKS)).also { link(it, cellar.racks, ::rack) } }
      .apply { children += TreeItem<Item<*>>(SubItem(CONSTANTS)).also { link(it, cellar.constants, ::const) } }

    private fun rack(rack: FxRack) = TreeItem<Item<*>>(RackItem(rack))
      .apply { children += TreeItem<Item<*>>(SubItem(ARGUMENTS)).also { link(it, rack.arguments, ::arg) } }
      .apply { children += TreeItem<Item<*>>(SubItem(INITIALIZERS)).also { link(it, rack.initializers, ::init) } }
      .apply { children += TreeItem<Item<*>>(SubItem(OUTPUTS)).also { link(it, rack.outputs, ::output) } }

    private fun const(constant: FxCellarConstant) = TreeItem<Item<*>>(CellarConstantItem(constant))
      .apply { link(this, constant.arguments, ::arg) }

    private fun output(output: FxOutput): TreeItem<Item<*>> = TreeItem(OutputItem(output))

    private fun init(initializer: FxInitializer) = TreeItem<Item<*>>(InitializerItem(initializer))
      .apply { link(this, initializer.arguments, ::arg) }

    private fun arg(argument: FxArgument): TreeItem<Item<*>> = TreeItem(when (argument) {
      is FxNull -> NullItem(argument)
      is FxConstRef -> ConstRefItem(argument)
      is FxLiteral -> LiteralItem(argument)
      is FxRef -> RefItem(argument)
    })

    private fun <E : FxEntity> link(item: TreeItem<Item<*>>, list: ObservableList<E>, func: (E) -> TreeItem<Item<*>>) {
      item.children += list.map(func)
      val weakItem = WeakReference(item)
      val listener = ListChangeListener<E> { c ->
        val ti = weakItem.get() ?: return@ListChangeListener
        while (c.next()) {
          if (c.wasRemoved()) {
            ti.children.remove(c.from, c.from + c.removedSize)
          }
          if (c.wasAdded()) {
            ti.children.addAll(c.from, c.addedSubList.map(func))
          }
          if (c.wasUpdated()) {
            ti.children.subList(c.from, c.to).forEach {
              fireEvent(it, TreeModificationEvent(TreeItem.treeNotificationEvent<Item<*>>(), it, it.value))
            }
          }
          if (c.wasPermutated()) {
            val orders = ti.children.identityMap { if (it >= c.from && it < c.to) c.getPermutation(it) else it }
            ti.children.sortWith(compareBy { orders[it] })
          }
        }
      }
      list.addListener(listener)
      FX_CLEANER.register(item) {
        Platform.runLater {
          list.removeListener(listener)
        }
      }
    }
  }
}
