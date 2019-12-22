package org.marid.fx.extensions

import javafx.beans.Observable
import javafx.beans.binding.Bindings.createIntegerBinding
import java.util.concurrent.Callable

internal object ExtensionHelpers {
  fun createIntBinding(callable: Callable<Int?>, vararg observables: Observable) = createIntegerBinding(callable, *observables)
}