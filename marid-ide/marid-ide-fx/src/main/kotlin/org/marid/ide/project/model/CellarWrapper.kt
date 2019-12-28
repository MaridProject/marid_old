package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.Cellar

class CellarWrapper() {

  constructor(cellar: Cellar) : this() {
    name.set(cellar.name)
    racks.setAll(cellar.racks.map(::RackWrapper))
    constants.setAll(cellar.constants.map(::CellarConstantWrapper))
  }

  val name = SimpleStringProperty(this, "name", "cellar")
  val racks = FXCollections.observableArrayList(RackWrapper::observables)
  val constants = FXCollections.observableArrayList(CellarConstantWrapper::observables)

  val observables = arrayOf(name, racks, constants)

  val cellar
    get() = Cellar(name.get())
      .also { it.racks.addAll(racks.map { r -> r.rack }) }
      .also { it.constants.addAll(constants.map(CellarConstantWrapper::constant)) }
}