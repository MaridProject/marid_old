package org.marid.ide.project.model

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import org.marid.runtime.model.*

sealed class ArgumentWrapper : Observable {

  abstract val observables: Array<Observable>
  abstract val argument: Argument

  override fun addListener(listener: InvalidationListener) {
    observables.forEach { it.addListener(listener) }
  }

  override fun removeListener(listener: InvalidationListener) {
    observables.forEach { it.removeListener(listener) }
  }
}

sealed class ConstantArgumentWrapper : ArgumentWrapper() {
  abstract override val argument: ConstantArgument
}

class ArgumentConstRefWrapper : ConstantArgumentWrapper() {

  val cellar = SimpleStringProperty(this, "cellar", "")
  val ref = SimpleStringProperty(this, "ref", "")

  override val observables: Array<Observable> = arrayOf(ref, cellar)
  override val argument: ConstantArgument = ArgumentConstRef(cellar.get(), ref.get())
}

class ArgumentLiteralWrapper(val type: ArgumentLiteral.Type) : ConstantArgumentWrapper() {

  val value = SimpleStringProperty(this, "value", "")

  override val observables: Array<Observable> = arrayOf(value)
  override val argument: ConstantArgument = ArgumentLiteral(type, value.get())
}

class ArgumentRefWrapper : ArgumentWrapper() {

  val cellar = SimpleStringProperty(this, "cellar", "")
  val rack = SimpleStringProperty(this, "rack", "")
  val ref = SimpleStringProperty(this, "ref", "")

  override val observables: Array<Observable> = arrayOf(cellar, rack, ref)
  override val argument: Argument = ArgumentRef(cellar.get(), rack.get(), ref.get())
}

class ArgumentNullWrapper : ArgumentWrapper() {

  override val observables: Array<Observable> = arrayOf()
  override val argument = ArgumentNull()
}