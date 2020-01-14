package org.marid.ide.project.xml

import javafx.collections.FXCollections
import org.marid.fx.xml.child
import org.marid.io.Xmls
import org.marid.xml.XmlStreams
import java.nio.file.Path
import kotlin.streams.toList

class XmlDependencies {

  val items = FXCollections.observableArrayList(XmlDependency::observables)
  val observables = arrayOf(items)

  fun load(path: Path) {
    Xmls.read(path) { items.setAll(XmlStreams.elementsByTag(it, "dependency").map(::XmlDependency).toList()) }
  }

  fun save(path: Path) {
    Xmls.writeFormatted("dependencies", { e -> items.forEach { i -> e.child("dependency").also(i::writeTo) } }, path)
  }
}