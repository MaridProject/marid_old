package org.marid.ide.child.project.tree

import com.sun.javafx.binding.StringConstant
import javafx.beans.value.ObservableValue
import org.marid.ide.project.model.FxCellar
import org.marid.ide.project.model.FxRack
import org.marid.ide.project.model.ResolvedTypeProvider
import java.lang.reflect.Type

sealed class Item : ResolvedTypeProvider {
  abstract val name: ObservableValue<String>
  abstract val factory: ObservableValue<String>
  abstract val value: ObservableValue<String>
}

class CellarItem(val cellar: FxCellar) : Item() {
  override val name: ObservableValue<String> get() = cellar.name
  override val factory: ObservableValue<String> = StringConstant.valueOf("")
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> get() = cellar.resolvedType
}

class RackItem(val rack: FxRack) : Item() {
  override val name: ObservableValue<String> get() = rack.name
  override val factory: ObservableValue<String> get() = rack.factory
  override val value: ObservableValue<String> = StringConstant.valueOf("")
  override val resolvedType: ObservableValue<Type> = rack.resolvedType
}