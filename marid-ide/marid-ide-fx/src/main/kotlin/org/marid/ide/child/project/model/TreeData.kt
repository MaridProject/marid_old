package org.marid.ide.child.project.model

import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.Event.fireEvent
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeItem.TreeModificationEvent
import org.marid.fx.extensions.FX_CLEANER
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.ide.project.model.*
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.lang.ref.WeakReference
import java.util.*

@Component
class TreeData(projectFactory: ObjectFactory<Project>) {

  val root = projectFactory.bean.winery.let {
    TreeItem<Item<*>>(WineryItem(it)).apply {
      link(this, it.cellars, ::item)
    }
  }

  private companion object {

    private fun item(cellar: FxCellar) = TreeItem<Item<*>>(CellarItem(cellar))
      .apply { children += TreeItem<Item<*>>(SubItem("Racks")).also { link(it, cellar.racks, ::item) } }
      .apply { children += TreeItem<Item<*>>(SubItem("Constants")).also { link(it, cellar.constants, ::item) } }

    private fun item(rack: FxRack) = TreeItem<Item<*>>(RackItem(rack))
      .apply { children += TreeItem<Item<*>>(SubItem("Arguments")).also { link(it, rack.arguments, ::item) } }
      .apply { children += TreeItem<Item<*>>(SubItem("Initializers")).also { link(it, rack.initializers, ::item) } }
      .apply { children += TreeItem<Item<*>>(SubItem("Outputs")).also { link(it, rack.outputs, ::item) } }

    private fun item(constant: FxCellarConstant) = TreeItem<Item<*>>(CellarConstantItem(constant))
      .apply { link(this, constant.arguments, ::item) }

    private fun item(output: FxOutput): TreeItem<Item<*>> = TreeItem(OutputItem(output))

    private fun item(initializer: FxInitializer) = TreeItem<Item<*>>(InitializerItem(initializer))
      .apply { link(this, initializer.arguments, ::item) }

    private fun item(argument: FxArgument): TreeItem<Item<*>> = TreeItem(when (argument) {
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
            ti.children.remove(c.from, c.to + 1)
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
            for (i in c.from until c.to) {
              Collections.swap(ti.children, i, c.getPermutation(i))
            }
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