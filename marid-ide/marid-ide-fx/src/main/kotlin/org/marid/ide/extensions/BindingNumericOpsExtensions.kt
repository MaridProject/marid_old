package org.marid.ide.extensions

import javafx.beans.binding.*
import javafx.beans.binding.Bindings.*
import javafx.beans.value.*


// minus --> subtract
fun ObservableDoubleValue.minus(v: Double): DoubleBinding = subtract(this, v) as DoubleBinding
fun Double.minus(v: ObservableDoubleValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableDoubleValue.minus(v: ObservableDoubleValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableDoubleValue.minus(v: Float): DoubleBinding = subtract(this, v) as DoubleBinding
fun Double.minus(v: ObservableFloatValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableDoubleValue.minus(v: ObservableFloatValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableDoubleValue.minus(v: Long): DoubleBinding = subtract(this, v) as DoubleBinding
fun Double.minus(v: ObservableLongValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableDoubleValue.minus(v: ObservableLongValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableDoubleValue.minus(v: Int): DoubleBinding = subtract(this, v) as DoubleBinding
fun Double.minus(v: ObservableIntegerValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableDoubleValue.minus(v: ObservableIntegerValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableFloatValue.minus(v: Double): DoubleBinding = subtract(this, v) as DoubleBinding
fun Float.minus(v: ObservableDoubleValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableFloatValue.minus(v: ObservableDoubleValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableFloatValue.minus(v: Float): FloatBinding = subtract(this, v) as FloatBinding
fun Float.minus(v: ObservableFloatValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableFloatValue.minus(v: ObservableFloatValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableFloatValue.minus(v: Long): FloatBinding = subtract(this, v) as FloatBinding
fun Float.minus(v: ObservableLongValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableFloatValue.minus(v: ObservableLongValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableFloatValue.minus(v: Int): FloatBinding = subtract(this, v) as FloatBinding
fun Float.minus(v: ObservableIntegerValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableFloatValue.minus(v: ObservableIntegerValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableLongValue.minus(v: Double): DoubleBinding = subtract(this, v) as DoubleBinding
fun Long.minus(v: ObservableDoubleValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableLongValue.minus(v: ObservableDoubleValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableLongValue.minus(v: Float): FloatBinding = subtract(this, v) as FloatBinding
fun Long.minus(v: ObservableFloatValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableLongValue.minus(v: ObservableFloatValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableLongValue.minus(v: Long): LongBinding = subtract(this, v) as LongBinding
fun Long.minus(v: ObservableLongValue): LongBinding = subtract(this, v) as LongBinding
fun ObservableLongValue.minus(v: ObservableLongValue): LongBinding = subtract(this, v) as LongBinding
fun ObservableLongValue.minus(v: Int): LongBinding = subtract(this, v) as LongBinding
fun Long.minus(v: ObservableIntegerValue): LongBinding = subtract(this, v) as LongBinding
fun ObservableLongValue.minus(v: ObservableIntegerValue): LongBinding = subtract(this, v) as LongBinding
fun ObservableIntegerValue.minus(v: Double): DoubleBinding = subtract(this, v) as DoubleBinding
fun Int.minus(v: ObservableDoubleValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableIntegerValue.minus(v: ObservableDoubleValue): DoubleBinding = subtract(this, v) as DoubleBinding
fun ObservableIntegerValue.minus(v: Float): FloatBinding = subtract(this, v) as FloatBinding
fun Int.minus(v: ObservableFloatValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableIntegerValue.minus(v: ObservableFloatValue): FloatBinding = subtract(this, v) as FloatBinding
fun ObservableIntegerValue.minus(v: Long): LongBinding = subtract(this, v) as LongBinding
fun Int.minus(v: ObservableLongValue): LongBinding = subtract(this, v) as LongBinding
fun ObservableIntegerValue.minus(v: ObservableLongValue): LongBinding = subtract(this, v) as LongBinding
fun ObservableIntegerValue.minus(v: Int): IntegerBinding = subtract(this, v) as IntegerBinding
fun Int.minus(v: ObservableIntegerValue): IntegerBinding = subtract(this, v) as IntegerBinding
fun ObservableIntegerValue.minus(v: ObservableIntegerValue): IntegerBinding = subtract(this, v) as IntegerBinding

// plus --> add
fun ObservableDoubleValue.plus(v: Double): DoubleBinding = add(this, v) as DoubleBinding
fun Double.plus(v: ObservableDoubleValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableDoubleValue.plus(v: ObservableDoubleValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableDoubleValue.plus(v: Float): DoubleBinding = add(this, v) as DoubleBinding
fun Double.plus(v: ObservableFloatValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableDoubleValue.plus(v: ObservableFloatValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableDoubleValue.plus(v: Long): DoubleBinding = add(this, v) as DoubleBinding
fun Double.plus(v: ObservableLongValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableDoubleValue.plus(v: ObservableLongValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableDoubleValue.plus(v: Int): DoubleBinding = add(this, v) as DoubleBinding
fun Double.plus(v: ObservableIntegerValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableDoubleValue.plus(v: ObservableIntegerValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableFloatValue.plus(v: Double): DoubleBinding = add(this, v) as DoubleBinding
fun Float.plus(v: ObservableDoubleValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableFloatValue.plus(v: ObservableDoubleValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableFloatValue.plus(v: Float): FloatBinding = add(this, v) as FloatBinding
fun Float.plus(v: ObservableFloatValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableFloatValue.plus(v: ObservableFloatValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableFloatValue.plus(v: Long): FloatBinding = add(this, v) as FloatBinding
fun Float.plus(v: ObservableLongValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableFloatValue.plus(v: ObservableLongValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableFloatValue.plus(v: Int): FloatBinding = add(this, v) as FloatBinding
fun Float.plus(v: ObservableIntegerValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableFloatValue.plus(v: ObservableIntegerValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableLongValue.plus(v: Double): DoubleBinding = add(this, v) as DoubleBinding
fun Long.plus(v: ObservableDoubleValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableLongValue.plus(v: ObservableDoubleValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableLongValue.plus(v: Float): FloatBinding = add(this, v) as FloatBinding
fun Long.plus(v: ObservableFloatValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableLongValue.plus(v: ObservableFloatValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableLongValue.plus(v: Long): LongBinding = add(this, v) as LongBinding
fun Long.plus(v: ObservableLongValue): LongBinding = add(this, v) as LongBinding
fun ObservableLongValue.plus(v: ObservableLongValue): LongBinding = add(this, v) as LongBinding
fun ObservableLongValue.plus(v: Int): LongBinding = add(this, v) as LongBinding
fun Long.plus(v: ObservableIntegerValue): LongBinding = add(this, v) as LongBinding
fun ObservableLongValue.plus(v: ObservableIntegerValue): LongBinding = add(this, v) as LongBinding
fun ObservableIntegerValue.plus(v: Double): DoubleBinding = add(this, v) as DoubleBinding
fun Int.plus(v: ObservableDoubleValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableIntegerValue.plus(v: ObservableDoubleValue): DoubleBinding = add(this, v) as DoubleBinding
fun ObservableIntegerValue.plus(v: Float): FloatBinding = add(this, v) as FloatBinding
fun Int.plus(v: ObservableFloatValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableIntegerValue.plus(v: ObservableFloatValue): FloatBinding = add(this, v) as FloatBinding
fun ObservableIntegerValue.plus(v: Long): LongBinding = add(this, v) as LongBinding
fun Int.plus(v: ObservableLongValue): LongBinding = add(this, v) as LongBinding
fun ObservableIntegerValue.plus(v: ObservableLongValue): LongBinding = add(this, v) as LongBinding
fun ObservableIntegerValue.plus(v: Int): IntegerBinding = add(this, v) as IntegerBinding
fun Int.plus(v: ObservableIntegerValue): IntegerBinding = add(this, v) as IntegerBinding
fun ObservableIntegerValue.plus(v: ObservableIntegerValue): IntegerBinding = add(this, v) as IntegerBinding

// times --> multiply
fun ObservableDoubleValue.times(v: Double): DoubleBinding = multiply(this, v) as DoubleBinding
fun Double.times(v: ObservableDoubleValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableDoubleValue.times(v: ObservableDoubleValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableDoubleValue.times(v: Float): DoubleBinding = multiply(this, v) as DoubleBinding
fun Double.times(v: ObservableFloatValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableDoubleValue.times(v: ObservableFloatValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableDoubleValue.times(v: Long): DoubleBinding = multiply(this, v) as DoubleBinding
fun Double.times(v: ObservableLongValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableDoubleValue.times(v: ObservableLongValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableDoubleValue.times(v: Int): DoubleBinding = multiply(this, v) as DoubleBinding
fun Double.times(v: ObservableIntegerValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableDoubleValue.times(v: ObservableIntegerValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableFloatValue.times(v: Double): DoubleBinding = multiply(this, v) as DoubleBinding
fun Float.times(v: ObservableDoubleValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableFloatValue.times(v: ObservableDoubleValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableFloatValue.times(v: Float): FloatBinding = multiply(this, v) as FloatBinding
fun Float.times(v: ObservableFloatValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableFloatValue.times(v: ObservableFloatValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableFloatValue.times(v: Long): FloatBinding = multiply(this, v) as FloatBinding
fun Float.times(v: ObservableLongValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableFloatValue.times(v: ObservableLongValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableFloatValue.times(v: Int): FloatBinding = multiply(this, v) as FloatBinding
fun Float.times(v: ObservableIntegerValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableFloatValue.times(v: ObservableIntegerValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableLongValue.times(v: Double): DoubleBinding = multiply(this, v) as DoubleBinding
fun Long.times(v: ObservableDoubleValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableLongValue.times(v: ObservableDoubleValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableLongValue.times(v: Float): FloatBinding = multiply(this, v) as FloatBinding
fun Long.times(v: ObservableFloatValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableLongValue.times(v: ObservableFloatValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableLongValue.times(v: Long): LongBinding = multiply(this, v) as LongBinding
fun Long.times(v: ObservableLongValue): LongBinding = multiply(this, v) as LongBinding
fun ObservableLongValue.times(v: ObservableLongValue): LongBinding = multiply(this, v) as LongBinding
fun ObservableLongValue.times(v: Int): LongBinding = multiply(this, v) as LongBinding
fun Long.times(v: ObservableIntegerValue): LongBinding = multiply(this, v) as LongBinding
fun ObservableLongValue.times(v: ObservableIntegerValue): LongBinding = multiply(this, v) as LongBinding
fun ObservableIntegerValue.times(v: Double): DoubleBinding = multiply(this, v) as DoubleBinding
fun Int.times(v: ObservableDoubleValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableIntegerValue.times(v: ObservableDoubleValue): DoubleBinding = multiply(this, v) as DoubleBinding
fun ObservableIntegerValue.times(v: Float): FloatBinding = multiply(this, v) as FloatBinding
fun Int.times(v: ObservableFloatValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableIntegerValue.times(v: ObservableFloatValue): FloatBinding = multiply(this, v) as FloatBinding
fun ObservableIntegerValue.times(v: Long): LongBinding = multiply(this, v) as LongBinding
fun Int.times(v: ObservableLongValue): LongBinding = multiply(this, v) as LongBinding
fun ObservableIntegerValue.times(v: ObservableLongValue): LongBinding = multiply(this, v) as LongBinding
fun ObservableIntegerValue.times(v: Int): IntegerBinding = multiply(this, v) as IntegerBinding
fun Int.times(v: ObservableIntegerValue): IntegerBinding = multiply(this, v) as IntegerBinding
fun ObservableIntegerValue.times(v: ObservableIntegerValue): IntegerBinding = multiply(this, v) as IntegerBinding

// div --> divide
fun ObservableDoubleValue.div(v: Double): DoubleBinding = divide(this, v) as DoubleBinding
fun Double.div(v: ObservableDoubleValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableDoubleValue.div(v: ObservableDoubleValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableDoubleValue.div(v: Float): DoubleBinding = divide(this, v) as DoubleBinding
fun Double.div(v: ObservableFloatValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableDoubleValue.div(v: ObservableFloatValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableDoubleValue.div(v: Long): DoubleBinding = divide(this, v) as DoubleBinding
fun Double.div(v: ObservableLongValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableDoubleValue.div(v: ObservableLongValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableDoubleValue.div(v: Int): DoubleBinding = divide(this, v) as DoubleBinding
fun Double.div(v: ObservableIntegerValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableDoubleValue.div(v: ObservableIntegerValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableFloatValue.div(v: Double): DoubleBinding = divide(this, v) as DoubleBinding
fun Float.div(v: ObservableDoubleValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableFloatValue.div(v: ObservableDoubleValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableFloatValue.div(v: Float): FloatBinding = divide(this, v) as FloatBinding
fun Float.div(v: ObservableFloatValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableFloatValue.div(v: ObservableFloatValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableFloatValue.div(v: Long): FloatBinding = divide(this, v) as FloatBinding
fun Float.div(v: ObservableLongValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableFloatValue.div(v: ObservableLongValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableFloatValue.div(v: Int): FloatBinding = divide(this, v) as FloatBinding
fun Float.div(v: ObservableIntegerValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableFloatValue.div(v: ObservableIntegerValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableLongValue.div(v: Double): DoubleBinding = divide(this, v) as DoubleBinding
fun Long.div(v: ObservableDoubleValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableLongValue.div(v: ObservableDoubleValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableLongValue.div(v: Float): FloatBinding = divide(this, v) as FloatBinding
fun Long.div(v: ObservableFloatValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableLongValue.div(v: ObservableFloatValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableLongValue.div(v: Long): LongBinding = divide(this, v) as LongBinding
fun Long.div(v: ObservableLongValue): LongBinding = divide(this, v) as LongBinding
fun ObservableLongValue.div(v: ObservableLongValue): LongBinding = divide(this, v) as LongBinding
fun ObservableLongValue.div(v: Int): LongBinding = divide(this, v) as LongBinding
fun Long.div(v: ObservableIntegerValue): LongBinding = divide(this, v) as LongBinding
fun ObservableLongValue.div(v: ObservableIntegerValue): LongBinding = divide(this, v) as LongBinding
fun ObservableIntegerValue.div(v: Double): DoubleBinding = divide(this, v) as DoubleBinding
fun Int.div(v: ObservableDoubleValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableIntegerValue.div(v: ObservableDoubleValue): DoubleBinding = divide(this, v) as DoubleBinding
fun ObservableIntegerValue.div(v: Float): FloatBinding = divide(this, v) as FloatBinding
fun Int.div(v: ObservableFloatValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableIntegerValue.div(v: ObservableFloatValue): FloatBinding = divide(this, v) as FloatBinding
fun ObservableIntegerValue.div(v: Long): LongBinding = divide(this, v) as LongBinding
fun Int.div(v: ObservableLongValue): LongBinding = divide(this, v) as LongBinding
fun ObservableIntegerValue.div(v: ObservableLongValue): LongBinding = divide(this, v) as LongBinding
fun ObservableIntegerValue.div(v: Int): IntegerBinding = divide(this, v) as IntegerBinding
fun Int.div(v: ObservableIntegerValue): IntegerBinding = divide(this, v) as IntegerBinding
fun ObservableIntegerValue.div(v: ObservableIntegerValue): IntegerBinding = divide(this, v) as IntegerBinding
