package org.marid.ide.project

import javafx.beans.property.SimpleStringProperty
import org.marid.fx.xml.get
import org.w3c.dom.Element

class Repository private constructor(val id: String, name: String) {

  constructor(element: Element) : this(element["id"], element["name"]) {
  }

  val name = SimpleStringProperty(this, "name", name)
  val observables = arrayOf(name)
}