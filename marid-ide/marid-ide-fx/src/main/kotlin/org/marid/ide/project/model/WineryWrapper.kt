package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.io.Xmls
import org.marid.runtime.model.Winery
import javax.xml.transform.Result

class WineryWrapper() {

  constructor(winery: Winery) : this() {
    name.set(winery.name)
    cellars.setAll(winery.cellars.map(::CellarWrapper))
  }

  val name = SimpleStringProperty(this, "name", "winery")
  val cellars = FXCollections.observableArrayList(CellarWrapper::observables)

  val winery
    get() = Winery(name.get())
      .also { it.cellars.addAll(cellars.map(CellarWrapper::cellar)) }

  fun save(result: Result) {
    Xmls.writeFormatted("winery", winery::writeTo, result)
  }
}