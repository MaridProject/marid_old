package org.marid.fx

import javafx.collections.ObservableList

fun <L: ObservableList<T>, T> L.plusAssign(e: T): L {
  add(e)
  return this
}