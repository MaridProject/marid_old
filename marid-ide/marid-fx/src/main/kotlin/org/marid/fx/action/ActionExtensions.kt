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

package org.marid.fx.action

import javafx.application.Platform.runLater
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.marid.fx.control.ToolButton
import org.marid.fx.extensions.FX_CLEANER
import org.marid.fx.extensions.identityMap
import org.marid.fx.extensions.map
import org.marid.fx.extensions.mapObject
import java.lang.ref.WeakReference
import java.util.*

fun String?.icon(size: Int): Image? = this?.let { Image(it, size.toDouble(), size.toDouble(), false, true) }

fun <C : Control> C.configure(action: Fx): C = this
  .apply { tooltipProperty().bind(action.description.mapObject { it?.let(::Tooltip) }) }
  .apply { disableProperty().bind(action.disabled) }
  .apply { visibleProperty().bind(action.visible) }

fun <L : Labeled> L.configure(action: Fx, size: Int = 24): L = this
  .apply { (this as Control).configure(action) }
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(size)) } }) }

fun <B : ButtonBase> B.configure(action: Fx, size: Int = 24): B = this
  .apply { (this as Labeled).configure(action, size) }
  .apply { onActionProperty().bind(action.handler) }

fun <B : ToggleButton> B.configure(action: Fx, size: Int = 24): B = this
  .apply { (this as ButtonBase).configure(action, size) }
  .apply { action.linkSelected(selectedProperty()) }

fun <B : ToolButton> B.configure(action: Fx, size: Int = 24): B = this
  .apply { (this as Button).configure(action, size) }
  .apply { textProperty().bind(SimpleStringProperty()) }
  .apply { tooltipProperty().bind(action.text.mapObject { it?.let(::Tooltip) }) }

fun <M : MenuItem> M.configure(action: Fx, size: Int = 20): M = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(size)) } }) }
  .apply { onActionProperty().bind(action.handler.map { v -> EventHandler { runLater { v?.handle(it) } } }) }
  .apply { acceleratorProperty().bind(action.accelerator) }
  .apply { disableProperty().bind(action.disabled) }
  .apply { visibleProperty().bind(action.visible) }

fun <M : CheckMenuItem> M.configure(action: Fx, size: Int = 20): M = this
  .apply { (this as MenuItem).configure(action, size) }
  .apply { action.linkSelected(selectedProperty()) }

fun <M : Menu> M.configure(action: Fx, size: Int = 20): M = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(size)) } }) }
  .apply { disableProperty().bind(action.disabled) }
  .apply { visibleProperty().bind(action.visible) }

fun <T : Tab> T.configure(action: Fx, size: Int = 18): T = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(size)) } }) }
  .apply { tooltipProperty().bind(action.description.mapObject { it?.let(::Tooltip) }) }
  .apply { disableProperty().bind(action.disabled) }

val Fx.button get() = if (selectedBound) toggleButton else Button().configure(this)
val Fx.toggleButton get() = ToggleButton().configure(this)
val Fx.toolButton get() = ToolButton().configure(this)
val Fx.label get() = Label().configure(this)
val Fx.menu get() = Menu().configure(this)
val Fx.menuItem: MenuItem
  get() = when {
    isEmpty -> SeparatorMenuItem()
    hasChildren -> menu.also { m ->
      val wm = WeakReference(m)
      val listener = ListChangeListener<Fx> { c ->
        val menu = wm.get() ?: return@ListChangeListener
        while (c.next()) {
          if (c.wasRemoved()) {
            menu.items.remove(c.from, c.from + c.removedSize)
          }
          if (c.wasAdded()) {
            menu.items.addAll(c.from, c.addedSubList.map { it.menuItem })
          }
          if (c.wasPermutated()) {
            val orders = menu.items.identityMap { if (it >= c.from && it < c.to) c.getPermutation(it) else it }
            menu.items.sortWith(compareBy { orders[it] })
          }
        }
      }
      val cd = children
      cd.addListener(listener)
      FX_CLEANER.register(m) {
        runLater { cd.removeListener(listener) }
      }
    }
    selectedBound -> checkMenuItem
    else -> MenuItem().configure(this)
  }
val Fx.checkMenuItem get() = CheckMenuItem().configure(this)
