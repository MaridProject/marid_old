package org.marid.ide.extensions

import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableValue
import java.util.concurrent.Callable

fun <T, R> ObservableValue<T>.map(func: (T) -> R): ObjectBinding<R> =
  Bindings.createObjectBinding(Callable { func(value)}, this)

fun <T> ObservableValue<T>.mapString(func: (T) -> String): StringBinding =
  Bindings.createStringBinding(Callable { func(value) }, this)