package org.marid.fx.action

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.marid.fx.control.ToolButton
import org.marid.fx.extensions.mapObject

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
  .apply { selectedProperty().bindBidirectional(action.selected) }

fun <B : ToolButton> B.configure(action: Fx, size: Int = 24): B = this
  .apply { (this as Button).configure(action, size) }
  .apply { textProperty().bind(SimpleStringProperty()) }
  .apply { tooltipProperty().bind(action.text.mapObject { it?.let(::Tooltip) }) }

fun <M : MenuItem> M.configure(action: Fx, size: Int = 20): M = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(size)) } }) }
  .apply { onActionProperty().bind(action.handler) }
  .apply { acceleratorProperty().bind(action.accelerator) }
  .apply { disableProperty().bind(action.disabled) }
  .apply { visibleProperty().bind(action.visible) }

fun <M : CheckMenuItem> M.configure(action: Fx, size: Int = 20): M = this
  .apply { (this as MenuItem).configure(action, size) }
  .apply { selectedProperty().bindBidirectional(action.selected) }

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

val Fx.button get() = Button().configure(this)
val Fx.toolButton get() = ToolButton().configure(this)
val Fx.label get() = Label().configure(this)
val Fx.menu get() = Menu().configure(this)
val Fx.menuItem get() = MenuItem().configure(this)
val Fx.checkMenuItem get() = CheckMenuItem().configure(this)
val Fx.toggleButton get() = ToggleButton().configure(this)