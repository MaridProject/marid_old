package org.marid.fx.action

import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import java.util.*

class FxAction(
  val text: StringProperty,
  val icon: ObjectProperty<Image>,
  val description: StringProperty,
  val accelerator: ObjectProperty<KeyCombination>,
  val handler: ObjectProperty<EventHandler<ActionEvent>>
) {
  operator fun component1(): String = text.get()
  operator fun component2(): Image = icon.get()
  operator fun component3(): String = description.get()
  operator fun component4(): KeyCombination = accelerator.get()
  operator fun component5(): EventHandler<ActionEvent> = handler.get()

  override fun hashCode(): Int = Objects.hash(component1(), component2(), component3(), component4(), component5())
  override fun equals(other: Any?): Boolean = when (other) {
    other === this -> true
    is FxAction -> {
      val (c1, c2, c3, c4, c5) = other
      c1 == component1() && c2 == component2() && c3 == component3() && c4 == component4() && c5 == component5()
    }
    else -> false
  }
}