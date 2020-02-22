package org.marid.ide.project

import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TableView
import org.marid.fx.i18n.localized
import org.marid.ide.common.IdePreferences
import org.marid.ide.project.model.FxCellarConstant
import org.marid.ide.project.model.FxRack
import org.springframework.stereotype.Component

@Component
class CellarPane(
  racksTable: CellarRacksTable,
  constantsTable: CellarConstantsTable,
  idePreferences: IdePreferences
) : TabPane(
  Tab(null, racksTable).also {
    it.isClosable = false
    it.textProperty().bind("Racks".localized)
  },
  Tab(null, constantsTable).also {
    it.isClosable = false
    it.textProperty().bind("Constants".localized)
  }
) {
  init {
    sideProperty().bind(idePreferences.primaryTabsSide)
  }
}

@Component
class CellarRacksTable : TableView<FxRack>() {

  init {

  }
}

@Component
class CellarConstantsTable : TableView<FxCellarConstant>() {

}