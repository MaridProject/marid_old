package org.marid.ide.child.project.winery

import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.util.Callback
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.ide.project.model.CellarWrapper
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class CellarsList(projectFactory: ObjectFactory<Project>) : ListView<CellarWrapper>(projectFactory.bean.winery.cellars) {

  init {
    cellFactory = Callback { _ ->
      object : ListCell<CellarWrapper>() {
        override fun updateItem(item: CellarWrapper?, empty: Boolean) {
          super.updateItem(item, empty)
          if (empty || item == null) {
            textProperty().unbind()
            graphic = null
          } else {
            textProperty().bind(item.name)
            graphic = ImageView(Image("icons/cellar.png"))
          }
        }
      }
    }
  }
}