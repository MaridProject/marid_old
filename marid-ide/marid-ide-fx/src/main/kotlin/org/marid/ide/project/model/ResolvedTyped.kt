package org.marid.ide.project.model

import javafx.beans.property.SimpleObjectProperty
import java.lang.reflect.Type

interface ResolvedTyped {
  val resolvedType: SimpleObjectProperty<Type>
}