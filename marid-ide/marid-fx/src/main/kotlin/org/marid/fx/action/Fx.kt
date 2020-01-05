package org.marid.fx.action

import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyCombination.keyCombination
import org.marid.fx.i18n.localized
import java.util.*

typealias Handler = EventHandler<ActionEvent>

class Fx private constructor(
  val text: SimpleStringProperty = SimpleStringProperty(),
  val icon: SimpleStringProperty = SimpleStringProperty(),
  val description: SimpleStringProperty = SimpleStringProperty(),
  val accelerator: SimpleObjectProperty<KeyCombination> = SimpleObjectProperty(),
  val handler: SimpleObjectProperty<Handler> = SimpleObjectProperty(),
  val selected: SimpleBooleanProperty = SimpleBooleanProperty()
) {
  constructor(
    text: String? = null,
    icon: String? = null,
    description: String? = null,
    key: String? = null,
    handler: ((ActionEvent) -> Unit)? = null,
    selected: Property<Boolean?>? = null
  ) : this(text = SimpleStringProperty()) {
    text?.also { this.text.bind(it.localized) }
    icon?.also { this.icon.bind(SimpleStringProperty(it)) }
    description?.also { this.description.bind(SimpleStringProperty(it)) }
    key?.also { this.accelerator.bind(SimpleObjectProperty(keyCombination(it))) }
    handler?.also { this.handler.bind(SimpleObjectProperty(EventHandler(it))) }
    selected?.also { this.selected.bindBidirectional(it) }
  }

  operator fun component1(): String? = text.get()
  operator fun component2(): String? = icon.get()
  operator fun component3(): String? = description.get()
  operator fun component4(): KeyCombination? = accelerator.get()
  operator fun component5(): EventHandler<ActionEvent>? = handler.get()

  override fun hashCode(): Int = Objects.hash(component1(), component2(), component3(), component4(), component5())
  override fun equals(other: Any?): Boolean = when (other) {
    other === this -> true
    is Fx -> {
      this.component1() == other.component1()
        && this.component2() == other.component2()
        && this.component3() == other.component3()
        && this.component4() == other.component4()
        && this.component5() == other.component5()
    }
    else -> false
  }

  fun text(text: String): Fx = also { this.text.bind(text.localized) }
  fun exactText(text: String): Fx = also { this.text.bind(SimpleStringProperty(text)) }
  fun text(text: ObservableValue<String>): Fx = also { this.text.bind(text) }
  fun icon(resource: String): Fx = also { this.icon.bind(SimpleStringProperty(resource)) }
  fun icon(resource: ObservableValue<String>) = also { this.icon.bind(resource) }
  fun description(text: String): Fx = also { this.description.bind(text.localized) }
  fun descriptionText(text: String): Fx = also { this.description.bind(SimpleStringProperty(text)) }
  fun description(text: ObservableValue<String>) = also { this.description.bind(text) }
  fun accelerator(keys: String) = also { this.accelerator.bind(SimpleObjectProperty(keyCombination(keys))) }
  fun accelerator(keys: KeyCombination) = also { this.accelerator.bind(SimpleObjectProperty(keys)) }
  fun accelerator(keys: ObservableValue<KeyCombination>) = also { this.accelerator.bind(keys) }
  fun handler(handler: EventHandler<ActionEvent>) = also { this.handler.bind(SimpleObjectProperty(handler)) }
  fun handler(handler: (ActionEvent) -> Unit) = also { this.handler.bind(SimpleObjectProperty(EventHandler(handler))) }
  fun handler(handler: ObservableValue<EventHandler<ActionEvent>>) = also { this.handler.bind(handler) }
  fun selected(selected: Property<Boolean?>) = also { this.selected.bindBidirectional(selected) }

}