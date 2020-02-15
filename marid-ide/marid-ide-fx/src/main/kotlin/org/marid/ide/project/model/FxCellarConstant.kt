package org.marid.ide.project.model

import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.CellarConstant
import org.marid.runtime.model.ConstantArgument

class FxCellarConstant : FxEntity(), CellarConstant {

  val factory = SimpleStringProperty(this, "factory")
  val selector = SimpleStringProperty(this, "selector")
  val name = SimpleStringProperty(this, "name")
  val arguments = FXCollections.observableArrayList(FxConstantArgument::observables)
  val observables = arrayOf<Observable>(factory, selector, name, arguments)

  override fun getArguments(): MutableList<out ConstantArgument> = arguments
  override fun setName(name: String) = this.name.set(name)
  override fun setFactory(factory: String) = this.factory.set(factory)
  override fun setSelector(selector: String) = this.selector.set(selector)
  override fun getName(): String = this.name.get()
  override fun getFactory(): String = this.factory.get()
  override fun getSelector(): String = this.selector.get()

  override fun addArgument(argument: ConstantArgument) {
    arguments.add(argument as FxConstantArgument)
  }
}