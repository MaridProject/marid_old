@CheckedFunctionalInterface(
    targetPackageName = "org.marid.io.function",
    interfacePrefix = "IO",
    checkedThrowableClasses = {IOException.class},
    wrapperExceptionClass = UncheckedIOException.class,
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
@CheckedFunctionalInterface(
    targetPackageName = "org.marid.io",
    interfacePrefix = "IO",
    checkedThrowableClasses = {IOException.class},
    wrapperExceptionClass = UncheckedIOException.class,
    functionalInterfaces = {
        Runnable.class
    }
)
package org.marid.io;

/*-
 * #%L
 * marid-util
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.marid.processors.CheckedFunctionalInterface;

import java.io.IOException;
import java.io.UncheckedIOException;
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
