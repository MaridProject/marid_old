package org.marid.fx.extensions

import javafx.beans.Observable
import javafx.beans.binding.*
import javafx.beans.binding.Bindings.*
import javafx.beans.value.ObservableIntegerValue
import javafx.collections.ObservableList
import java.util.*
import java.util.concurrent.Callable

fun <O : Observable, R> O.objectBnd(f: (O) -> R): ObjectBinding<R> = createObjectBinding(Callable { f(this) }, this)
fun <O : Observable> O.doubleBnd(f: (O) -> Double): DoubleBinding = createDoubleBinding(Callable { f(this) }, this)
fun <O : Observable> O.floatBnd(f: (O) -> Float): FloatBinding = createFloatBinding(Callable { f(this) }, this)
fun <O : Observable> O.longBnd(f: (O) -> Long): LongBinding = createLongBinding(Callable { f(this) }, this)
fun <O : Observable> O.intBnd(f: (O) -> Int): IntegerBinding = createIntegerBinding(Callable { f(this) }, this)
fun <O : Observable> O.booleanBnd(f: (O) -> Boolean): BooleanBinding = createBooleanBinding(Callable { f(this) }, this)
fun <O : Observable> O.stringBnd(f: (O) -> String): StringBinding = createStringBinding(Callable { f(this) }, this)

fun <E> ObservableList<E>.get_(index: Int): ObjectBinding<E> = valueAt(this, index)
fun <E> ObservableList<E>.get_(index: ObservableIntegerValue): ObjectBinding<E> = valueAt(this, index)

val <E> ObservableList<E>.last_: ObjectBinding<E> get() = createObjectBinding(Callable { get(lastIndex) }, this)
val <E> ObservableList<E>.first_: ObjectBinding<E> get() = valueAt(this, 0)

val ObservableList<*>.empty_: BooleanBinding get() = isEmpty(this)
val ObservableList<*>.notEmpty_: BooleanBinding get() = isNotEmpty(this)
val ObservableList<*>.size_: IntegerBinding get() = size(this)
val <E : CharSequence> ObservableList<E>.nonMultiLine: ObservableList<E> get() = filtered { it.lines().size == 1 }
fun <E> ObservableList<E>.nonMultiLine(text: (E) -> String): ObservableList<E> = filtered { text(it).lines().size == 1 }

fun String.format_(vararg args: Any): StringExpression = Bindings.format(this, args)
fun String.format_(locale: Locale, vararg args: Any): StringExpression = Bindings.format(locale, this, args)