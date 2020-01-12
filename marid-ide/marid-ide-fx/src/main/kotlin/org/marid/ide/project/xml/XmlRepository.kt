package org.marid.ide.project.xml

import javafx.beans.property.SimpleStringProperty
import org.marid.fx.xml.get
import org.marid.fx.xml.set
import org.w3c.dom.Element

class XmlRepository(name: String, url: String) {

  val name = SimpleStringProperty(this, "name", name)
  val url = SimpleStringProperty(this, "url", url)

  val observables = arrayOf(this.name, this.url)

  constructor(element: Element) : this(
    name = element["name"],
    url = element["url"]
  )

  fun writeTo(element: Element) {
    element["name"] = name.get()
    element["url"] = url.get()
  }
}