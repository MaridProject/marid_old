package org.marid.ide.project.xml

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.ide.project.model.CellarWrapper
import org.marid.io.Xmls
import org.marid.runtime.model.Winery
import java.nio.file.Path

class XmlWinery {

  val name = SimpleStringProperty(this, "name", "winery")
  val group = SimpleStringProperty(this, "group", "org.marid")
  val version = SimpleStringProperty(this, "version", "1.0")
  val cellars = FXCollections.observableArrayList(CellarWrapper::observables)
  val observables = arrayOf(name, cellars)

  private val winery
    get() = Winery(group.get(), name.get(), version.get())
      .also { it.cellars.addAll(cellars.map(CellarWrapper::cellar)) }

  fun load(path: Path) {
    val winery = Xmls.read(path, ::Winery)
    group.set(winery.group)
    name.set(winery.name)
    cellars.setAll(winery.cellars.map(::CellarWrapper))
  }

  fun save(path: Path) {
    Xmls.writeFormatted("winery", winery::writeTo, path)
  }
}