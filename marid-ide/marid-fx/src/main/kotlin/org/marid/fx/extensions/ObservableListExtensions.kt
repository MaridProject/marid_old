package org.marid.fx.extensions

import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.ObjectBinding
import javafx.collections.ObservableList
import java.util.concurrent.Callable

val <E> ObservableList<E>.bLast: ObjectBinding<E> get() = createObjectBinding(Callable {get(lastIndex)}, this)