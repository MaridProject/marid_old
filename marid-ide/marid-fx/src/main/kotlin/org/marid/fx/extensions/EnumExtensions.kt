package org.marid.fx.extensions

import javafx.geometry.Side
import kotlin.math.max

inline val <reified E : Enum<E>> E.progress: Double get() = ordinal.toDouble() / enumValues<E>().size.toDouble()

inline fun <reified E : Enum<E>> E.progress(subProgress: Double): Double {
  val step = 1.0 / enumValues<E>().size.toDouble()
  val value = max(step * subProgress, step)
  return progress + value
}

inline fun <reified E : Enum<E>, reified P : Enum<P>> E.progress(subItem: P): Double = progress(subItem.progress)

val Side.icon get() = when(this) {
  Side.TOP -> "icons/side/top.png"
  Side.BOTTOM -> "icons/side/bottom.png"
  Side.LEFT -> "icons/side/left.png"
  Side.RIGHT -> "icons/side/right.png"
}