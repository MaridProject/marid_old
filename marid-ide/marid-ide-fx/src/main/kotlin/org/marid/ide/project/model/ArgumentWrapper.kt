package org.marid.ide.project.model

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.marid.runtime.model.*
import java.lang.reflect.Type

sealed class ArgumentWrapper : Observable {

  abstract val observables: Array<Observable>
  abstract val argument: Argument
  val argumentType = SimpleObjectProperty<Type>(this, "type", Any::class.java)

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

class ArgumentConstRefWrapper() : ConstantArgumentWrapper() {

  constructor(ref: ArgumentConstRef) : this() {
    this.cellar.set(ref.cellar)
    this.ref.set(ref.ref)
  }

  val cellar = SimpleStringProperty(this, "cellar", "")
  val ref = SimpleStringProperty(this, "ref", "")

  override val observables: Array<Observable> = arrayOf(ref, cellar)
  override val argument: ArgumentConstRef get() = ArgumentConstRef(cellar.get(), ref.get())
}

class ArgumentLiteralWrapper(val type: ArgumentLiteral.Type) : ConstantArgumentWrapper() {

  constructor(literal: ArgumentLiteral) : this(literal.type) {
    this.value.set(literal.value)
  }

  val value = SimpleStringProperty(this, "value", "")

  override val observables: Array<Observable> = arrayOf(value)
  override val argument: ArgumentLiteral get() = ArgumentLiteral(type, value.get())
}

class ArgumentRefWrapper() : ArgumentWrapper() {

  constructor(ref: ArgumentRef) : this() {
    this.cellar.set(ref.cellar)
    this.rack.set(ref.rack)
    this.ref.set(ref.ref)
  }

  val cellar = SimpleStringProperty(this, "cellar", "")
  val rack = SimpleStringProperty(this, "rack", "")
  val ref = SimpleStringProperty(this, "ref", "")

  override val observables: Array<Observable> = arrayOf(cellar, rack, ref)
  override val argument: ArgumentRef get() = ArgumentRef(cellar.get(), rack.get(), ref.get())
}

class ArgumentNullWrapper : ArgumentWrapper() {

  override val observables: Array<Observable> = arrayOf()
  override val argument: ArgumentNull get() = ArgumentNull()
}

object ArgumentWrapperFactory {
  fun argumentWrapper(argument: Argument) = when (argument) {
    is ArgumentLiteral -> ArgumentLiteralWrapper(argument)
    is ArgumentConstRef -> ArgumentConstRefWrapper(argument)
    is ArgumentRef -> ArgumentRefWrapper(argument)
    is ArgumentNull -> ArgumentNullWrapper()
    else -> throw IllegalArgumentException("Unknown argument: $argument")
  }

  fun constantArgumentWrapper(argument: ConstantArgument) = when (argument) {
    is ArgumentLiteral -> ArgumentLiteralWrapper(argument)
    is ArgumentConstRef -> ArgumentConstRefWrapper(argument)
    else -> throw IllegalArgumentException("Unknown argument: $argument")
  }
}