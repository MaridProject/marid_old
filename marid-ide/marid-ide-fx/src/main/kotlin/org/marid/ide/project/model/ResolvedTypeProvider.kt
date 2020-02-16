package org.marid.ide.project.model

import javafx.beans.value.ObservableValue
import java.lang.reflect.Type

interface ResolvedTypeProvider {
  val resolvedType: ObservableValue<Type>
}