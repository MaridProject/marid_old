package org.marid.fx.action

import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.marid.fx.extensions.mapObject

fun String?.icon(size: Int): Image? = this?.let { Image(it, size.toDouble(), size.toDouble(), false, true) }

fun <C : Control> C.configure(action: FxAction): C = this
  .apply { tooltipProperty().bind(action.description.mapObject { it?.let { Tooltip(it) } }) }

fun <L : Labeled> L.configure(action: FxAction, size: Int = 24): L = this
  .apply { (this as Control).configure(action) }
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(size)) } }) }

fun <B: ButtonBase> B.configure(action: FxAction, size: Int = 24): B = this
  .apply { (this as Labeled).configure(action, size) }
  .apply { onActionProperty().bind(action.handler) }

fun <B : ToggleButton> B.configure(action: FxAction, size: Int = 24): B = this
  .apply { (this as ButtonBase).configure(action, size) }
  .apply { selectedProperty().bindBidirectional(action.selected) }

fun <M : MenuItem> M.configure(action: FxAction, size: Int = 20): M = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(size)) } }) }
  .apply { onActionProperty().bind(action.handler) }
  .apply { acceleratorProperty().bind(action.accelerator) }

fun <M : CheckMenuItem> M.configure(action: FxAction, size: Int = 20): M = this
  .apply { (this as MenuItem).configure(action, size) }
  .apply { selectedProperty().bindBidirectional(action.selected) }

fun <M : Menu> M.configure(action: FxAction, size: Int = 20): M = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(size)) } }) }

fun <T : Tab> T.configure(action: FxAction, size: Int = 20): T = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(size)) } }) }
  .apply { tooltipProperty().bind(action.description.mapObject { it?.let { Tooltip(it) } }) }