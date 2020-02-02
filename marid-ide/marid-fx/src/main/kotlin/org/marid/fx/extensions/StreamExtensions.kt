package org.marid.fx.extensions

import java.util.stream.Collectors
import java.util.stream.Stream

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

inline fun <reified T> Stream<T>.toTypedArray(): Array<T> = toArray { n -> arrayOfNulls<T>(n) }