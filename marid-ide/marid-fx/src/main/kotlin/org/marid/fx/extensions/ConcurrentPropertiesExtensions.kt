/*-
 * #%L
 * marid-fx
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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

package org.marid.fx.extensions

import javafx.application.Platform
import javafx.beans.binding.IntegerBinding
import javafx.beans.value.*
import java.util.concurrent.CompletableFuture

typealias ObservableIntValue = ObservableIntegerValue
typealias IntBinding = IntegerBinding

val ObservableIntegerValue.safe: CompletableFuture<Int>
  get() = if (Platform.isFxApplicationThread()) {
    CompletableFuture.completedFuture(get())
  } else {
    val future = CompletableFuture<Int>()
    Platform.runLater { future.complete(get()) }
    future
  }

val ObservableLongValue.safe: CompletableFuture<Long>
  get() = if (Platform.isFxApplicationThread()) {
    CompletableFuture.completedFuture(get())
  } else {
    val future = CompletableFuture<Long>()
    Platform.runLater { future.complete(get()) }
    future
  }

val ObservableFloatValue.safe: CompletableFuture<Float>
  get() = if (Platform.isFxApplicationThread()) {
    CompletableFuture.completedFuture(get())
  } else {
    val future = CompletableFuture<Float>()
    Platform.runLater { future.complete(get()) }
    future
  }

val ObservableDoubleValue.safe: CompletableFuture<Double>
  get() = if (Platform.isFxApplicationThread()) {
    CompletableFuture.completedFuture(get())
  } else {
    val future = CompletableFuture<Double>()
    Platform.runLater { future.complete(get()) }
    future
  }

val ObservableStringValue.safe: CompletableFuture<String>
  get() = if (Platform.isFxApplicationThread()) {
    CompletableFuture.completedFuture(get())
  } else {
    val future = CompletableFuture<String>()
    Platform.runLater { future.complete(get()) }
    future
  }

val ObservableBooleanValue.safe: CompletableFuture<Boolean>
  get() = if (Platform.isFxApplicationThread()) {
    CompletableFuture.completedFuture(get())
  } else {
    val future = CompletableFuture<Boolean>()
    Platform.runLater { future.complete(get()) }
    future
  }

val <T> ObservableValue<T>.safe: CompletableFuture<T>
  get() = if (Platform.isFxApplicationThread()) {
    CompletableFuture.completedFuture(value)
  } else {
    val future = CompletableFuture<T>()
    Platform.runLater { future.complete(value) }
    future
  }
