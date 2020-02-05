package org.marid.fx.extensions

fun IntRange.progress(v: Int): Double = (v - first).toDouble() / (last - first).toDouble()
fun LongRange.progress(v: Long): Double = (v - first).toDouble() / (last - first).toDouble()