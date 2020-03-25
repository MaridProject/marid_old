package org.marid.fx.extensions

import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.*
import javafx.beans.binding.StringExpression
import javafx.beans.property.*
import javafx.beans.value.ObservableIntegerValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.util.*
import javafx.beans.binding.Bindings.createBooleanBinding as bindBool
import javafx.beans.binding.Bindings.createDoubleBinding as bindDouble
import javafx.beans.binding.Bindings.createFloatBinding as bindFloat
import javafx.beans.binding.Bindings.createIntegerBinding as bindInt
import javafx.beans.binding.Bindings.createLongBinding as bindLong
import javafx.beans.binding.Bindings.createObjectBinding as bind
import javafx.beans.binding.Bindings.createStringBinding as bindString
import javafx.beans.binding.BooleanBinding as BBinding
import javafx.beans.binding.DoubleBinding as DBinding
import javafx.beans.binding.FloatBinding as FBinding
import javafx.beans.binding.IntegerBinding as IBinding
import javafx.beans.binding.LongBinding as LBinding
import javafx.beans.binding.ObjectBinding as OBinding
import javafx.beans.binding.StringBinding as SBinding

fun <O : Observable, R> O.bindObject(f: (O) -> R): OBinding<R> = bind({ f(this) }, this)
fun <O : Observable> O.bindDouble(f: (O) -> Double): DBinding = bindDouble({ f(this) }, this)
fun <O : Observable> O.bindFloat(f: (O) -> Float): FBinding = bindFloat({ f(this) }, this)
fun <O : Observable> O.bindLong(f: (O) -> Long): LBinding = bindLong({ f(this) }, this)
fun <O : Observable> O.bindInt(f: (O) -> Int): IBinding = bindInt({ f(this) }, this)
fun <O : Observable> O.bindBoolean(f: (O) -> Boolean): BBinding = bindBool({ f(this) }, this)
fun <O : Observable> O.bindString(f: (O) -> String): SBinding = bindString({ f(this) }, this)

val <E> ObservableList<E>.unmodified: ObservableList<E> get() = FXCollections.unmodifiableObservableList(this)
fun <E> ObservableList<E>.bindAt(index: Int): OBinding<E> = valueAt(this, index)
fun <E> ObservableList<E>.bindAt(index: ObservableIntegerValue): OBinding<E> = valueAt(this, index)
fun <E> ObservableList<E>.singleLined(text: (E) -> String): ObservableList<E> = filtered { text(it).lines().size == 1 }
val <E> ObservableList<E>.bindLast: OBinding<E?> get() = bind({ if (isEmpty()) null else get(lastIndex) }, this)
val <E> ObservableList<E>.bindFirst: OBinding<E?> get() = valueAt(this, 0)
val ObservableList<*>.bindEmpty: BBinding get() = isEmpty(this)
val ObservableList<*>.bindNotEmpty: BBinding get() = isNotEmpty(this)
val ObservableList<*>.bindSize: IBinding get() = size(this)
val <E : CharSequence> ObservableList<E>.singleLined: ObservableList<E> get() = filtered { it.lines().size == 1 }

fun String.bindFormat(vararg args: Any): StringExpression = Bindings.format(this, *args)
fun String.bindFormat(locale: Locale, vararg args: Any): StringExpression = Bindings.format(locale, this, *args)

val <T> T?.readOnlyProp get() = ReadOnlyObjectWrapper(this).readOnlyProperty
val String?.readOnlyProp get() = ReadOnlyStringWrapper(this).readOnlyProperty
val Long.readOnlyProp get() = ReadOnlyLongWrapper(this).readOnlyProperty
val Int.readOnlyProp get() = ReadOnlyIntegerWrapper(this).readOnlyProperty
val Double.readOnlyProp get() = ReadOnlyDoubleWrapper(this).readOnlyProperty
val Float.readOnlyProp get() = ReadOnlyFloatWrapper(this).readOnlyProperty
val Boolean.readOnlyProp get() = ReadOnlyBooleanWrapper(this).readOnlyProperty

fun <R> List<Observable>.bound(func: () -> R): OBinding<R> = bind({ func() }, *toTypedArray())
fun List<Observable>.doubleBound(func: () -> Double): DBinding = bindDouble({ func() }, *toTypedArray())
fun List<Observable>.floatBound(func: () -> Float): FBinding = bindFloat({ func() }, *toTypedArray())
fun List<Observable>.longBound(func: () -> Long): LBinding = bindLong({ func() }, *toTypedArray())
fun List<Observable>.intBound(func: () -> Int): IBinding = bindInt({ func() }, *toTypedArray())
fun List<Observable>.booleanBound(func: () -> Boolean): BBinding = bindBool({ func() }, *toTypedArray())
fun List<Observable>.stringBound(func: () -> String): SBinding = bindString({ func() }, *toTypedArray())

@Suppress("UNCHECKED_CAST") val DoubleProperty.asDoubleProperty: Property<Double>
  get() = this as Property<Double>
@Suppress("UNCHECKED_CAST") val ReadOnlyDoubleProperty.asDoubleProperty: ReadOnlyProperty<Double>
  get() = this as ReadOnlyProperty<Double>
