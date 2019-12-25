package org.marid.ide.project

import javafx.beans.property.SimpleStringProperty
import org.marid.fx.i18n.localized
import java.util.*

class Project {

  val id = UUID.randomUUID().toString()
  val name = SimpleStringProperty(this, "name", "New project".localized.get())
  val observables = arrayOf(name)

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