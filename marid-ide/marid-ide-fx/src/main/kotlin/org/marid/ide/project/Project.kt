package org.marid.ide.project

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.fx.i18n.localized
import org.marid.ide.project.model.CellarWrapper
import org.marid.runtime.model.Winery
import java.util.*

class Project {

  val id = UUID.randomUUID().toString()
  val name = SimpleStringProperty(this, "name", "New project".localized.get())
  val cellars = FXCollections.observableArrayList(CellarWrapper::observables)

  val observables = arrayOf(name)

  val winery
    get() = Winery(name.get())
      .also { it.cellars.addAll(cellars.map { c -> c.cellar }) }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    return when (other) {
      other === this -> true
      is Project -> other.id == id
      else -> false
    }
  }
}