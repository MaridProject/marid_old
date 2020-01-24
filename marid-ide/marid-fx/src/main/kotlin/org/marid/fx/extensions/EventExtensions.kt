package org.marid.fx.extensions

import javafx.event.Event
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.input.SwipeEvent
import javafx.scene.input.TouchEvent
import java.awt.event.KeyEvent

val Event.isAltered
  get() = when (this) {
    is KeyEvent -> isAltDown || isAltGraphDown || isControlDown || isMetaDown || isShiftDown
    is MouseEvent -> isAltDown || isControlDown || isMetaDown || isShiftDown
    is ScrollEvent -> isAltDown || isControlDown || isMetaDown || isShiftDown
    is TouchEvent -> isAltDown || isControlDown || isMetaDown || isShiftDown
    is SwipeEvent -> isAltDown || isControlDown || isMetaDown || isShiftDown
    else -> false
  }