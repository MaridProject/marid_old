package org.marid.ide.project.xml

import javafx.beans.property.SimpleStringProperty
import org.marid.fx.xml.get
import org.marid.fx.xml.set
import org.w3c.dom.Element

class XmlEntry(key: String, value: String) {

  val key = SimpleStringProperty(this, "key", key)
  val value = SimpleStringProperty(this, "value", value)
  val observables = arrayOf(this.key, this.value)

  constructor(element: Element) : this(element["key"], element.textContent)

  fun writeTo(element: Element) {
    element["key"] = key.get()
    element.textContent = value.get()
  }
}