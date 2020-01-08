package org.marid.ide.project.xml

import javafx.collections.FXCollections
import org.marid.fx.xml.child
import org.marid.io.Xmls
import org.marid.xml.XmlStreams
import java.nio.file.Path
import kotlin.streams.toList

class XmlRepositories {

  val items = FXCollections.observableArrayList(XmlRepository::observables)
  val observables = arrayOf(items)

  fun load(path: Path) {
    Xmls.read(path) { items.setAll(XmlStreams.elementsByTag(it, "repository").map(::XmlRepository).toList()) }
  }

  fun save(path: Path) {
    Xmls.writeFormatted("repositories", { e -> items.forEach { i -> e.child("repository").also(i::writeTo) } }, path)
  }
}