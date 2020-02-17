package org.marid.ide.child.project.tree

import javafx.collections.ListChangeListener
import javafx.event.Event
import javafx.event.Event.fireEvent
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeItem.TreeModificationEvent
import javafx.scene.control.TreeItem.valueChangedEvent
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.ide.project.model.*
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class TreeData(projectFactory: ObjectFactory<Project>) {

  val root = TreeItem<Item<*>>(WineryItem(projectFactory.bean.winery))

  init {
    val winery = root.value.entity as FxWinery
    winery.cellars.forEach { cellar -> root.children += cellarItem(cellar) }
    winery.cellars.addListener(ListChangeListener { c ->
      while (c.next()) {
        if (c.wasRemoved()) {
          root.children.remove(c.from, c.from + c.removedSize)
        }
        if (c.wasAdded()) {
          root.children.addAll(c.from, c.addedSubList.map(::cellarItem))
        }
        if (c.wasUpdated()) {
          root.children.subList(c.from, c.to).forEach {
            fireEvent(it, TreeModificationEvent(valueChangedEvent<Item<*>>(), it, it.value))
          }
        }
        if (c.wasPermutated()) {

        }
      }
    })
  }

  private fun cellarItem(cellar: FxCellar): TreeItem<Item<*>> {
    val cellarItem = TreeItem<Item<*>>(CellarItem(cellar))
    cellar.racks.forEach { rack -> cellarItem.children += rackItem(rack) }
    cellar.constants.forEach { constant -> cellarItem.children += constantItem(constant) }
    return cellarItem
  }

  private fun rackItem(rack: FxRack): TreeItem<Item<*>> {
    val rackItem = TreeItem<Item<*>>(RackItem(rack))
    rack.arguments.forEachIndexed { i, arg -> rackItem.children += argumentItem("arg$i", arg) }
    rack.inputs.forEach { input -> rackItem.children += inputItem(input) }
    rack.initializers.forEach { initializer -> rackItem.children += initializerItem(initializer) }
    rack.outputs.forEach { output -> rackItem.children += outputItem(output) }
    return rackItem
  }

  private fun constantItem(constant: FxCellarConstant): TreeItem<Item<*>> {
    val constantItem = TreeItem<Item<*>>(CellarConstantItem(constant))
    constant.arguments.forEachIndexed { i, arg -> constantItem.children += argumentItem("arg$i", arg) }
    return constantItem
  }

  private fun inputItem(input: FxInput): TreeItem<Item<*>> = TreeItem(InputItem(input))
  private fun outputItem(output: FxOutput): TreeItem<Item<*>> = TreeItem(OutputItem(output))

  private fun initializerItem(initializer: FxInitializer): TreeItem<Item<*>> {
    val initializerItem = TreeItem<Item<*>>(InitializerItem(initializer))
    initializer.arguments.forEachIndexed { i, arg -> initializerItem.children += argumentItem("arg$i", arg) }
    return initializerItem
  }

  private fun argumentItem(arg: String, argument: FxArgument): TreeItem<Item<*>> = TreeItem(when (argument) {
    is FxNull -> NullItem(arg, argument)
    is FxConstRef -> ConstRefItem(arg, argument)
    is FxLiteral -> LiteralItem(arg, argument)
    is FxRef -> RefItem(arg, argument)
  })
}