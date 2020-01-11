package org.marid.fx.control

import javafx.scene.Node
import javafx.scene.control.Button

open class ToolButton : Button {
  constructor(): super()
  constructor(text: String?): super(text)
  constructor(text: String?, graphic: Node?): super(text, graphic)

  init {
    isFocusTraversable = false
  }
}