package org.marid.ide.project.model

import javafx.beans.property.SimpleObjectProperty
import org.marid.runtime.model.AbstractEntity
import java.lang.reflect.Type

abstract class FxEntity : AbstractEntity(), ResolvedTyped {
  override val resolvedType = SimpleObjectProperty<Type>(this, "resolvedType", Void.TYPE)
}