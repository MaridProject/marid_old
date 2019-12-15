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

fun <E> ObservableList<E>.bGet(index: Int): ObjectBinding<E> = valueAt(this, index)
fun <E> ObservableList<E>.bGet(index: ObservableIntegerValue): ObjectBinding<E> = valueAt(this, index)

val <E> ObservableList<E>.bLast: ObjectBinding<E> get() = createObjectBinding(Callable { get(lastIndex) }, this)
val <E> ObservableList<E>.bFirst: ObjectBinding<E> get() = valueAt(this, 0)

val ObservableList<*>.bEmpty: BooleanBinding get() = isEmpty(this)
val ObservableList<*>.bNotEmpty: BooleanBinding get() = isNotEmpty(this)
val ObservableList<*>.bSize: IntegerBinding get() = size(this)

fun String.bFormat(vararg args: Any): StringExpression = Bindings.format(this, args)
fun String.bFormat(locale: Locale, vararg args: Any): StringExpression = Bindings.format(locale, this, args)