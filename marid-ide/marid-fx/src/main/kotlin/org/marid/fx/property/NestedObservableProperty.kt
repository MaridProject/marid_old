package org.marid.fx.property

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty

class NestedObservableProperty<T : Observable> : SimpleObjectProperty<T> {

  constructor() : super()
  constructor(initialValue: T) : super(initialValue)
  constructor(bean: Any, name: String) : super(bean, name)
  constructor(bean: Any, name: String, initialValue: T) : super(bean, name, initialValue)

  private val observer = InvalidationListener { fireValueChangedEvent() }

  init {
    value?.also { it.addListener(observer) }
    addListener { _, o, n ->
      o?.removeListener(observer)
      n?.addListener(observer)
    }
  }
}