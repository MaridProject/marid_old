package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import org.marid.fx.xml.get
import org.marid.fx.xml.set
import org.w3c.dom.Element
import java.util.*

class FxRepository(name: String, url: String) {

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

  override fun hashCode(): Int = Objects.hash(name.get(), url.get())
  override fun equals(other: Any?): Boolean = other === this || when (other) {
    is FxRepository -> arrayOf(name.get(), url.get()).contentEquals(arrayOf(other.name.get(), other.url.get()))
    else -> false
  }
}