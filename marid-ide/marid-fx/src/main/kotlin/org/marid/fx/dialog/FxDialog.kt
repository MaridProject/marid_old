package org.marid.fx.dialog

import javafx.scene.Node
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.StageStyle
import javafx.stage.Window
import javafx.util.Callback
import org.marid.fx.action.label
import org.marid.fx.dialog.FxDialogProp.Companion.fx
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class FxDialog<T : FxDialogData>(private val instance: T) : Dialog<T>() {

  init {
    initOwner(Window.getWindows().asReversed().let { ws -> ws.firstOrNull { it.isShowing } ?: ws.first() })
    initModality(Modality.WINDOW_MODAL)
    initStyle(StageStyle.UTILITY)

    dialogPane.buttonTypes += listOf(ButtonType.APPLY, ButtonType.CLOSE)

    dialogPane.content = GridPane().apply {
      hgap = 10.0
      vgap = 10.0
      columnConstraints += ColumnConstraints()
      columnConstraints += ColumnConstraints().apply {
        isFillWidth = true
        hgrow = Priority.ALWAYS
      }

      @Suppress("UNCHECKED_CAST") val type: KClass<T> = instance::class as KClass<T>
      type.memberProperties
        .flatMap { p -> p.annotations.filterIsInstance<FxDialogProp>().map { p to it } }
        .forEachIndexed { i, (prop, annotation) ->
          val node = prop.get(instance) as Node
          addRow(i, annotation.fx.label.also { it.labelFor = node }, node)
        }

      dialogPane.lookupButton(ButtonType.APPLY).disableProperty().bind(instance.validation.invalid)

      resultConverter = Callback {
        if (it.buttonData == ButtonBar.ButtonData.APPLY) {
          instance
        } else {
          null
        }
      }
    }

    instance::class.constructors
  }
}