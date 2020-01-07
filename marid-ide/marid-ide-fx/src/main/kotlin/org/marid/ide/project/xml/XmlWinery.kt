package org.marid.ide.project.xml

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.ide.project.model.CellarWrapper
import org.marid.io.Xmls
import org.marid.runtime.model.Winery
import java.nio.file.Path
import javax.xml.transform.stream.StreamResult

class XmlWinery {

  val name = SimpleStringProperty(this, "name")
  val cellars = FXCollections.observableArrayList(CellarWrapper::observables)
  val observables = arrayOf(name, cellars)

  private val winery
    get() = Winery(name.get())
      .also { it.cellars.addAll(cellars.map(CellarWrapper::cellar)) }

  fun load(path: Path) {
    val winery = Xmls.read(path, ::Winery)
    name.set(winery.name)
    cellars.setAll(winery.cellars.map(::CellarWrapper))
  }

  fun save(path: Path) {
    Xmls.writeFormatted("winery", winery::writeTo, StreamResult(path.toFile()))
  }
}