package org.marid.ide.project.model

import javafx.beans.property.SimpleObjectProperty
import org.marid.runtime.model.AbstractEntity
import org.marid.runtime.model.ModelObjectFactory
import java.lang.reflect.Type

abstract class FxEntity : AbstractEntity(), ResolvedTypeProvider {

  override val resolvedType = SimpleObjectProperty<Type>(this, "resolvedType", Void.TYPE)

  override fun modelObjectFactory(): ModelObjectFactory = FxModelObjectFactory
}