/*-
 * #%L
 * marid-fx
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.marid.fx.dialog

import javafx.scene.Node
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.*
import javafx.util.Callback
import org.marid.fx.action.label
import org.marid.fx.dialog.FxDialogProp.Companion.fx
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class FxDialog<T : FxDialogData>(private val instance: T) : Dialog<T>() {

  init {
    initOwner(Window.getWindows().filterIsInstance<Stage>().find { StageFriend.isPrimary(it) })
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
