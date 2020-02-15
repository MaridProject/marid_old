package org.marid.ide.project.model

import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.Cellar
import org.marid.runtime.model.Winery

class FxWinery : FxEntity(), Winery {

  val group = SimpleStringProperty(this, "group")
  val name = SimpleStringProperty(this, "name")
  val version = SimpleStringProperty(this, "version")
  val cellars = FXCollections.observableArrayList(FxCellar::observables)
  val observables = arrayOf<Observable>(group, name, version, cellars)

  override fun getGroup(): String = this.group.get()
  override fun setName(name: String) = this.name.set(name)
  override fun getName(): String = this.name.get()
  override fun getVersion(): String = this.version.get()
  override fun setGroup(group: String) = this.group.set(group)
  override fun setVersion(version: String) = this.version.set(version)
  override fun getCellars(): MutableList<out Cellar> = cellars

  override fun addCellar(cellar: Cellar) {
    cellars.add(cellar as FxCellar)
  }
}