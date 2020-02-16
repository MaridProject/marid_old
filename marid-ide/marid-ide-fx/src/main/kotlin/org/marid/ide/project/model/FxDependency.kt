package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import org.marid.fx.xml.get
import org.marid.fx.xml.set
import org.w3c.dom.Element

class FxDependency(group: String, artifact: String, version: String) {

  val group = SimpleStringProperty(this, "group", group)
  val artifact = SimpleStringProperty(this, "artifact", artifact)
  val version = SimpleStringProperty(this, "version", version)

  val observables = arrayOf(this.group, this.artifact, this.version)

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

  fun matches(dep: FxDependency): Boolean = dep.group.get() == group.get() && dep.artifact.get() == artifact.get()
  val isBlank get() = group.get().isBlank() || artifact.get().isBlank() || version.get().isBlank()
  fun copyFrom(dep: FxDependency) {
    group.set(dep.group.get())
    artifact.set(dep.artifact.get())
    version.set(dep.version.get())
  }

  operator fun component1(): String = group.get()
  operator fun component2(): String = artifact.get()
  operator fun component3(): String = version.get()
}