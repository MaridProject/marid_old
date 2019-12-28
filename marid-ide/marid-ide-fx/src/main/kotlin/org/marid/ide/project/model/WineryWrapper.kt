package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.io.Xmls
import org.marid.runtime.model.Winery
import javax.xml.transform.Result

class WineryWrapper() {

  val name = SimpleStringProperty(this, "name", "winery")
  val cellars = FXCollections.observableArrayList(CellarWrapper::observables)

  val winery
    get() = Winery(name.get())
      .also { it.cellars.addAll(cellars.map { c -> c.cellar }) }

  fun save(result: Result) {
    Xmls.writeFormatted("winery", { winery.writeTo(it) }, result)
  }
}