package org.marid.fx.action

import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.marid.fx.extensions.mapObject

fun String?.icon(size: Int): Image? = this?.let { Image(it, size.toDouble(), size.toDouble(), false, true) }

fun <C : Control> C.configure(action: FxAction): C = this
  .apply { tooltipProperty().bind(action.description.mapObject { it?.let { Tooltip(it) } }) }

fun <L : Labeled> L.configure(action: FxAction): L = this
  .apply { (this as Control).configure(action) }
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(24)) } }) }

fun <B : ToggleButton> B.configure(action: FxAction): B = this
  .apply { (this as Labeled).configure(action) }
  .apply { selectedProperty().bindBidirectional(action.selected) }

fun <M : MenuItem> M.configure(action: FxAction): M = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(20)) } }) }
  .apply { onActionProperty().bind(action.handler) }
  .apply { acceleratorProperty().bind(action.accelerator) }

fun <M : CheckMenuItem> M.configure(action: FxAction): M = this
  .apply { (this as MenuItem).configure(action) }
  .apply { selectedProperty().bindBidirectional(action.selected) }

fun <M : Menu> M.configure(action: FxAction): M = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(20)) } }) }

fun <T : Tab> T.configure(action: FxAction): T = this
  .apply { textProperty().bind(action.text) }
  .apply { graphicProperty().bind(action.icon.mapObject { it?.let { ImageView(it.icon(20)) } }) }
  .apply { tooltipProperty().bind(action.description.mapObject { it?.let { Tooltip(it) } }) }