package org.marid.fx.extensions

import java.util.stream.Collectors
import java.util.stream.Stream

fun <T, K, V> Stream<T>.toImmutableMap(key: (T) -> K, value: (T) -> V): Map<K, V> =
  collect(Collectors.toUnmodifiableMap(key, value))

fun <T, K, V> Stream<T>.toImmutableMap(key: (T) -> K, value: (T) -> V, merger: (V, V) -> V): Map<K, V> =
  collect(Collectors.toUnmodifiableMap(key, value, merger))

fun <T, K, V> Stream<T>.toMap(key: (T) -> K, value: (T) -> V): MutableMap<K, V> =
  collect(Collectors.toMap(key, value))

fun <T, K, V> Stream<T>.toMap(key: (T) -> K, value: (T) -> V, merger: (V, V) -> V): MutableMap<K, V> =
  collect(Collectors.toMap(key, value, merger))

fun <T, K, V, M : MutableMap<K, V>> Stream<T>.toMap(key: (T) -> K, value: (T) -> V, merger: (V, V) -> V, factory: () -> M): M =
  collect(Collectors.toMap(key, value, merger, factory))