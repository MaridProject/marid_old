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

import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.reflect.KClass

fun <T, K, V> Stream<T>.toImmutableMap(key: (T) -> K, value: (T) -> V): Map<K, V> =
  collect(Collectors.toUnmodifiableMap(key, value))

fun <T, K, V> Stream<T>.toImmutableMap(key: (T) -> K, value: (T) -> V, merger: (V, V) -> V): Map<K, V> =
  collect(Collectors.toUnmodifiableMap(key, value, merger))

fun <K, V, T : Pair<K, V>> Stream<T>.toImmutableMap(): Map<K, V> =
  collect(Collectors.toUnmodifiableMap({ it.first }, { it.second }))

fun <K, V, T : Pair<K, V>> Stream<T>.toImmutableMap(merger: (V, V) -> V): Map<K, V> =
  collect(Collectors.toUnmodifiableMap({ it.first }, { it.second }, { a, b -> merger(a, b) }))

fun <T, K, V> Stream<T>.toMap(key: (T) -> K, value: (T) -> V): MutableMap<K, V> =
  collect(Collectors.toMap(key, value))

fun <K, V, T : Pair<K, V>> Stream<T>.toMap(): Map<K, V> =
  collect(Collectors.toMap({ it.first }, { it.second }))

fun <K, V, T : Pair<K, V>> Stream<T>.toMap(merger: (V, V) -> V): Map<K, V> =
  collect(Collectors.toMap({ it.first }, { it.second }, { a, b -> merger(a, b) }))

fun <T, K, V> Stream<T>.toMap(key: (T) -> K, value: (T) -> V, merger: (V, V) -> V): MutableMap<K, V> =
  collect(Collectors.toMap(key, value, merger))

fun <T, K, V, M : MutableMap<K, V>> Stream<T>.toCustomMap(key: (T) -> K, value: (T) -> V, merger: (V, V) -> V, factory: () -> M): M =
  collect(Collectors.toMap(key, value, merger, factory))

fun <K, V, T : Pair<K, V>, M : MutableMap<K, V>> Stream<T>.toCustomMap(merger: (V, V) -> V, factory: () -> M): M =
  collect(Collectors.toMap({ it.first }, { it.second }, merger, factory))

fun <T> Stream<T>.toImmutableList(): List<T> = collect(Collectors.toUnmodifiableList())
fun <T> Stream<T>.toList(): MutableList<T> = collect(Collectors.toList())
fun <T, C : Collection<T>> Stream<T>.toCollection(factory: () -> C): C = collect(Collectors.toCollection(factory))

fun <T, R> Stream<T>.tryMap(func: (T) -> R, errorHandler: (T, Throwable) -> Unit): Stream<R> = flatMap { v: T ->
  try {
    Stream.of(func(v))
  } catch (e: Throwable) {
    errorHandler(v, e)
    Stream.empty<R>()
  }
}

inline fun <reified T> Stream<T>.toTypedArray(): Array<T> = toArray { n -> arrayOfNulls<T>(n) }

fun <T : Any> Stream<*>.typeFilter(c: KClass<T>): Stream<T> = filter { c.isInstance(it) }.map { c.java.cast(it) }
