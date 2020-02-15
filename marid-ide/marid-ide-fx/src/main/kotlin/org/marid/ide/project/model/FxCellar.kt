package org.marid.ide.project.model

import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.Cellar
import org.marid.runtime.model.CellarConstant
import org.marid.runtime.model.Rack

class FxCellar : FxEntity(), Cellar {

  val name = SimpleStringProperty(this, "name", "")
  val constants = FXCollections.observableArrayList(FxCellarConstant::observables)
  val racks = FXCollections.observableArrayList(FxRack::observables)
  val observables = arrayOf<Observable>(name, constants, racks, resolvedType)

  override fun setName(name: String) = this.name.set(name)
  override fun getName(): String = this.name.get()
  override fun getConstants(): MutableList<out CellarConstant> = constants
  override fun getRacks(): MutableList<out Rack> = racks

  override fun addConstant(constant: CellarConstant) {
    constants.add(constant as FxCellarConstant)
  }

  override fun addRack(rack: Rack) {
    racks.add(rack as FxRack)
  }
}