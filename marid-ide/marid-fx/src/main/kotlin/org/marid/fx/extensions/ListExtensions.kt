package org.marid.fx.extensions

import java.util.*

fun <E> MutableList<E>.toTop(index: Int) {
  if (index > 0) {
    val map = this
      .mapIndexed { i, e ->
        e to when {
          i == index -> 0
          i < index -> i + 1
          else -> i
        }
      }
      .fold(IdentityHashMap<E, Int>()) { acc, (e, i) -> acc.also { it[e] = i } }
    sortWith(compareBy(map::get))
  }
}

fun <E> MutableList<E>.toBottom(index: Int) {
  if (index < size - 1) {
    val map = this
      .mapIndexed { i, e ->
        e to when {
          i == index -> size - 1
          i > index -> i - 1
          else -> i
        }
      }
      .fold(IdentityHashMap<E, Int>()) { acc, (e, i) -> acc.also { it[e] = i } }
    sortWith(compareBy(map::get))
  }
}

fun <E> MutableList<E>.up(index: Int) {
  if (index > 0) {
    val map = this
      .mapIndexed { i, e ->
        e to when (i) {
          index -> i - 1
          index - 1 -> i + 1
          else -> i
        }
      }
      .fold(IdentityHashMap<E, Int>()) { acc, (e, i) -> acc.also { it[e] = i } }
    sortWith(compareBy(map::get))
  }
}