package org.marid.ide.project.model

import javafx.collections.FXCollections
import org.marid.fx.xml.child
import org.marid.io.Xmls
import org.marid.xml.XmlStreams
import java.nio.file.Path
import kotlin.streams.toList

class FxDependencies {

  val items = FXCollections.observableArrayList(FxDependency::observables)
  val observables = arrayOf(items)

  fun load(path: Path) {
    Xmls.read(path) { items.setAll(XmlStreams.elementsByTag(it, "dependency").map(::FxDependency).toList()) }
  }

  fun save(path: Path) {
    Xmls.writeFormatted("dependencies", { e -> items.forEach { i -> e.child("dependency").also(i::writeTo) } }, path)
  }

  fun loadDefault() {
    items.removeIf { STANDARD_DEPS.any(it::matches) }
    items.addAll(0, STANDARD_DEPS)
  }

  companion object {
    val STANDARD_DEPS = listOf(
      FxDependency("org.marid", "marid-racks", "\${marid.version}"),
      FxDependency("org.marid", "marid-db", "\${marid.version}"),
      FxDependency("org.marid", "marid-proto", "\${marid.version}")
    )
  }
}