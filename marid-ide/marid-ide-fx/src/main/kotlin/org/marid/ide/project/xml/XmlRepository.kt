package org.marid.ide.project.xml

import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import org.marid.fx.xml.get
import org.marid.fx.xml.set
import org.marid.xml.XmlStreams
import org.w3c.dom.Element
import java.util.*

class XmlRepository private constructor(val id: String, name: String, url: String, val params: XmlEntries) {

  val name = SimpleStringProperty(this, "name", name)
  val url = SimpleStringProperty(this, "url", url)
  val observables = arrayOf<Observable>(this.name, this.url) + this.params.observables

  constructor(element: Element) : this(
    id = element["id"],
    name = element["name"],
    url = element["url"],
    params = XmlStreams.elementsByTag(element, "params")
      .findFirst()
      .map(::XmlEntries)
      .orElseGet { XmlEntries() }
  )

  constructor(name: String, url: String, params: Map<String, String>) : this(
    UUID.randomUUID().toString(),
    name,
    url,
    XmlEntries(*params.map { (k, v) -> XmlEntry(k, v) }.toTypedArray())
  )

  fun writeTo(element: Element) {
    element["id"] = id
    element["name"] = name.get()
    element["url"] = url.get()
  }
}