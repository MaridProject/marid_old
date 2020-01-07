package org.marid.ide.project.xml

import javafx.collections.FXCollections
import org.marid.ide.project.Repository
import java.nio.file.Path

class XmlRepositories {

  val items = FXCollections.observableArrayList(Repository::observables)
  val observables = arrayOf(items)

  fun load(path: Path) {
  }

  fun save(path: Path) {
  }
}