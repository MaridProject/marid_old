@CheckedFunctionalInterfaces({
    @CheckedFunctionalInterface(
        targetPackageName = "org.marid.misc",
        interfacePrefix = "ClassLoading",
        wrapperExceptionClass = IllegalArgumentException.class,
        checkedThrowableClasses = {ClassNotFoundException.class},
        functionalInterfaces = {
            Supplier.class,
            Function.class,
            BiFunction.class,
            Consumer.class,
            BiConsumer.class,
            Predicate.class,
            BiPredicate.class
        }
    ),
    @CheckedFunctionalInterface(
        targetPackageName = "org.marid.misc",
        interfacePrefix = "Invocation",
        wrapperExceptionClass = UndeclaredThrowableException.class,
        checkedThrowableClasses = {Throwable.class},
        functionalInterfaces = {
            // consumers
            Consumer.class,
            IntConsumer.class,
            LongConsumer.class,
            DoubleConsumer.class,

            // bi consumers
            BiConsumer.class,
            ObjIntConsumer.class,
            ObjLongConsumer.class,
            ObjDoubleConsumer.class,

            // functions
            Function.class,
            IntFunction.class,
            LongFunction.class,
            DoubleFunction.class,
            ToIntFunction.class,
            ToLongFunction.class,
            ToDoubleFunction.class,
            IntToLongFunction.class,
            IntToDoubleFunction.class,
            LongToIntFunction.class,
            LongToDoubleFunction.class,
            DoubleToIntFunction.class,
            DoubleToLongFunction.class,

            // bi functions
            BiFunction.class,
            ToIntBiFunction.class,
            ToLongBiFunction.class,
            ToDoubleBiFunction.class,

            // operators
            BinaryOperator.class,
            UnaryOperator.class,
            DoubleBinaryOperator.class,
            DoubleUnaryOperator.class,
            IntBinaryOperator.class,
            IntUnaryOperator.class,
            LongBinaryOperator.class,
            LongUnaryOperator.class,

            // predicates
            Predicate.class,
            IntPredicate.class,
            LongPredicate.class,
            DoublePredicate.class,

            // bi predicates
            BiPredicate.class,

            // suppliers
            Supplier.class,
            BooleanSupplier.class,
            IntSupplier.class,
            LongSupplier.class,
            DoubleSupplier.class
        }
    ),
    @CheckedFunctionalInterface(
        targetPackageName = "org.marid.misc",
        interfacePrefix = "Reflective",
        wrapperExceptionClass = IllegalStateException.class,
        checkedThrowableClasses = {ReflectiveOperationException.class},
        functionalInterfaces = {
            // consumers
            Consumer.class,
            IntConsumer.class,
            LongConsumer.class,
            DoubleConsumer.class,

            // bi consumers
            BiConsumer.class,
            ObjIntConsumer.class,
            ObjLongConsumer.class,
            ObjDoubleConsumer.class,

            // functions
            Function.class,
            IntFunction.class,
            LongFunction.class,
            DoubleFunction.class,
            ToIntFunction.class,
            ToLongFunction.class,
            ToDoubleFunction.class,
            IntToLongFunction.class,
            IntToDoubleFunction.class,
            LongToIntFunction.class,
            LongToDoubleFunction.class,
            DoubleToIntFunction.class,
            DoubleToLongFunction.class,

            // bi functions
            BiFunction.class,
            ToIntBiFunction.class,
            ToLongBiFunction.class,
            ToDoubleBiFunction.class,

            // operators
            BinaryOperator.class,
            UnaryOperator.class,
            DoubleBinaryOperator.class,
            DoubleUnaryOperator.class,
            IntBinaryOperator.class,
            IntUnaryOperator.class,
            LongBinaryOperator.class,
            LongUnaryOperator.class,

            // predicates
            Predicate.class,
            IntPredicate.class,
            LongPredicate.class,
            DoublePredicate.class,

            // bi predicates
            BiPredicate.class,

            // suppliers
            Supplier.class,
            BooleanSupplier.class,
            IntSupplier.class,
            LongSupplier.class,
            DoubleSupplier.class
        }
    )
})
package org.marid.misc;

import org.marid.processors.CheckedFunctionalInterface;
import org.marid.processors.CheckedFunctionalInterfaces;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;