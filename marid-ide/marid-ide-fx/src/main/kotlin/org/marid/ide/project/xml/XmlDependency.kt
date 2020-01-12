package org.marid.ide.project.xml

import javafx.beans.property.SimpleStringProperty
import org.marid.fx.xml.get
import org.marid.fx.xml.set
import org.w3c.dom.Element

class XmlDependency private constructor(group: String, artifact: String, version: String) {

  val group = SimpleStringProperty(this, "group", group)
  val artifact = SimpleStringProperty(this, "artifact", artifact)
  val version = SimpleStringProperty(this, "version", version)

  val observables = arrayOf(group, artifact, version)

  constructor(element: Element) : this(
    group = element["group"],
    artifact = element["artifact"],
    version = element["version"]
  )

  fun writeTo(element: Element) {
    element["group"] = group.get()
    element["artifact"] = artifact.get()
    element["version"] = version.get()
  }
}