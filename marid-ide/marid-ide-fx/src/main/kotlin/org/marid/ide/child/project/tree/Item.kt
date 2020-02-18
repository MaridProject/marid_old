package org.marid.ide.child.project.tree

import com.sun.javafx.binding.StringConstant
import javafx.beans.value.ObservableValue
import org.marid.fx.extensions.bindFormat
import org.marid.fx.extensions.stringBound
import org.marid.ide.project.model.*
import java.lang.reflect.Type

sealed class Item<E : FxEntity> : ResolvedTypeProvider, Comparable<Item<*>> {
  abstract val name: ObservableValue<String>
  abstract val factory: ObservableValue<String>
  abstract val value: ObservableValue<String>
  abstract val entity: E
  protected abstract val order: Int
  override fun compareTo(other: Item<*>) = compareBy(Item<*>::order, { it.name.value }).compare(this, other)
}

class WineryItem(override val entity: FxWinery) : Item<FxWinery>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> get() = entity.group
  override val value: ObservableValue<String> get() = entity.version
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
  override val order: Int get() = 0
}

class CellarItem(override val entity: FxCellar) : Item<FxCellar>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> = StringConstant.valueOf("")
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
  override val order: Int get() = 0
}

class RackItem(override val entity: FxRack) : Item<FxRack>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> get() = entity.factory
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
  override val order: Int get() = 0
}

class CellarConstantItem(override val entity: FxCellarConstant) : Item<FxCellarConstant>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> = "%s.%s".bindFormat(entity.factory, entity.selector)
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
  override val order: Int get() = 1
}

class InitializerItem(override val entity: FxInitializer) : Item<FxInitializer>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> = StringConstant.valueOf("")
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
  override val order: Int get() = 2
}

class OutputItem(override val entity: FxOutput) : Item<FxOutput>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> = StringConstant.valueOf("")
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
  override val order: Int get() = 3
}

sealed class ArgumentItem<E : FxArgument>() : Item<E>() {
  override val order: Int get() = 0
  override val name: ObservableValue<String> get() = entity.name
}

sealed class ConstantArgumentItem<E : FxConstantArgument> : ArgumentItem<E>()

class NullItem(override val entity: FxNull) : ConstantArgumentItem<FxNull>() {
  override val factory: ObservableValue<String> = StringConstant.valueOf("")
  override val value: ObservableValue<String> = StringConstant.valueOf("null")
  override val resolvedType: ObservableValue<Type> = entity.resolvedType
}

class ConstRefItem(override val entity: FxConstRef) : ConstantArgumentItem<FxConstRef>() {
  override val factory: ObservableValue<String> get() = entity.cellar
  override val value: ObservableValue<String> get() = entity.ref
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
}

class LiteralItem(override val entity: FxLiteral) : ConstantArgumentItem<FxLiteral>() {
  override val factory: ObservableValue<String> get() = entity.type.asString()
  override val value: ObservableValue<String> get() = entity.value
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
}

class RefItem(override val entity: FxRef) : ArgumentItem<FxRef>() {
  override val factory: ObservableValue<String> = listOf(entity.cellar, entity.rack).stringBound { factory(entity) }
  override val value: ObservableValue<String> get() = entity.ref
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
}

private fun factory(arg: FxArgument) = when (arg) {
  is FxNull -> ""
  is FxConstRef -> arg.getCellar()
  is FxLiteral -> arg.getType().name
  is FxRef -> arg.getCellar() + "." + arg.getRack()
}

private fun value(arg: FxArgument) = when (arg) {
  is FxNull -> "null"
  is FxConstRef -> arg.getRef()
  is FxLiteral -> arg.getValue()
  is FxRef -> arg.getRef()
}