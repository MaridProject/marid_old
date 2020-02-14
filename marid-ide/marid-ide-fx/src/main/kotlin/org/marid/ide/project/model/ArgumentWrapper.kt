package org.marid.ide.project.model

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.marid.runtime.model.*
import java.lang.reflect.Type

sealed class ArgumentWrapper : Observable {

  abstract val observables: Array<Observable>
  abstract val argument: ArgumentImpl
  val argumentType = SimpleObjectProperty<Type>(this, "type", Any::class.java)

  override fun addListener(listener: InvalidationListener) {
    observables.forEach { it.addListener(listener) }
  }

  override fun removeListener(listener: InvalidationListener) {
    observables.forEach { it.removeListener(listener) }
  }
}

sealed class ConstantArgumentWrapper : ArgumentWrapper() {
  abstract override val argument: AbstractConstant
}

class ArgumentConstRefWrapper() : ConstantArgumentWrapper() {

  constructor(ref: ConstRefImpl) : this() {
    this.cellar.set(ref.cellar)
    this.ref.set(ref.ref)
  }

  val cellar = SimpleStringProperty(this, "cellar", "")
  val ref = SimpleStringProperty(this, "ref", "")

  override val observables: Array<Observable> = arrayOf(ref, cellar)
  override val argument: ConstRefImpl get() = ConstRefImpl(cellar.get(), ref.get())
}

class ArgumentLiteralWrapper(val type: LiteralImpl.Type) : ConstantArgumentWrapper() {

  constructor(literal: LiteralImpl) : this(literal.type) {
    this.value.set(literal.value)
  }

  val value = SimpleStringProperty(this, "value", "")

  override val observables: Array<Observable> = arrayOf(value)
  override val argument: LiteralImpl get() = LiteralImpl(type, value.get())
}

class ArgumentRefWrapper() : ArgumentWrapper() {

  constructor(ref: RefImpl) : this() {
    this.cellar.set(ref.cellar)
    this.rack.set(ref.rack)
    this.ref.set(ref.ref)
  }

  val cellar = SimpleStringProperty(this, "cellar", "")
  val rack = SimpleStringProperty(this, "rack", "")
  val ref = SimpleStringProperty(this, "ref", "")

  override val observables: Array<Observable> = arrayOf(cellar, rack, ref)
  override val argument: RefImpl get() = RefImpl(cellar.get(), rack.get(), ref.get())
}

class ArgumentNullWrapper : ArgumentWrapper() {

  override val observables: Array<Observable> = arrayOf()
  override val argument: NullImpl get() = NullImpl()
}

object ArgumentWrapperFactory {
  fun argumentWrapper(argument: ArgumentImpl) = when (argument) {
    is LiteralImpl -> ArgumentLiteralWrapper(argument)
    is ConstRefImpl -> ArgumentConstRefWrapper(argument)
    is RefImpl -> ArgumentRefWrapper(argument)
    is NullImpl -> ArgumentNullWrapper()
    else -> throw IllegalArgumentException("Unknown argument: $argument")
  }

  fun constantArgumentWrapper(argument: AbstractConstant) = when (argument) {
    is LiteralImpl -> ArgumentLiteralWrapper(argument)
    is ConstRefImpl -> ArgumentConstRefWrapper(argument)
    else -> throw IllegalArgumentException("Unknown argument: $argument")
  }
}