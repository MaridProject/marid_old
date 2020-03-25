package org.marid.fx.extensions

import javafx.scene.control.Dialog

val <T> Dialog<T>.value: T? get() = showAndWait().orElse(null)
fun <T, R> Dialog<T>.value(f: (T) -> R?): R? = showAndWait().map(f).orElse(null)