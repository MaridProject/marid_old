package org.marid.ide.child.project.model

import com.sun.javafx.binding.ObjectConstant
import com.sun.javafx.binding.StringConstant
import javafx.beans.value.ObservableValue
import javafx.scene.control.TreeItem
import org.marid.fx.extensions.bindFormat
import org.marid.fx.extensions.stringBound
import org.marid.fx.i18n.localized
import org.marid.ide.project.model.*
import java.lang.reflect.Type
import kotlin.reflect.KClass

sealed class Item<E : FxEntity> : ResolvedTypeProvider, Comparable<Item<*>> {
  abstract val name: ObservableValue<String>
  abstract val factory: ObservableValue<String>
  abstract val value: ObservableValue<String>
  abstract val entity: E
  override fun compareTo(other: Item<*>) = name.value.compareTo(other.name.value)
}

class SubItem(val kind: Kind) : Item<FxNull>() {
  override val name: ObservableValue<String> = kind.label.localized
  override val factory: ObservableValue<String> = StringConstant.valueOf("")
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val entity: FxNull get() = FxNull
  override val resolvedType: ObservableValue<Type> = ObjectConstant.valueOf(Void.TYPE)

  enum class Kind(val label: String) {
    CONSTANTS("Constants"),
    RACKS("Racks"),
    ARGUMENTS("Arguments"),
    INITIALIZERS("Initializers"),
    OUTPUTS("Outputs")
  }
}

class WineryItem(override val entity: FxWinery) : Item<FxWinery>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> get() = entity.group
  override val value: ObservableValue<String> get() = entity.version
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
}

class CellarItem(override val entity: FxCellar) : Item<FxCellar>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> = StringConstant.valueOf("")
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
}

class RackItem(override val entity: FxRack) : Item<FxRack>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> get() = entity.factory
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
}

class CellarConstantItem(override val entity: FxCellarConstant) : Item<FxCellarConstant>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> = "%s.%s".bindFormat(entity.factory, entity.selector)
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
}

class InitializerItem(override val entity: FxInitializer) : Item<FxInitializer>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> = StringConstant.valueOf("")
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
}

class OutputItem(override val entity: FxOutput) : Item<FxOutput>() {
  override val name: ObservableValue<String> get() = entity.name
  override val factory: ObservableValue<String> = StringConstant.valueOf("")
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = entity.resolvedType
}

sealed class ArgumentItem<E : FxArgument> : Item<E>() {
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

@Suppress("UNCHECKED_CAST")
fun <T : Item<*>> TreeItem<Item<*>>.ancestor(type: KClass<T>): TreeItem<T>? {
  if (type.isInstance(value)) {
    return this as TreeItem<T>;

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

  }
  return parent?.ancestor(type)
}
