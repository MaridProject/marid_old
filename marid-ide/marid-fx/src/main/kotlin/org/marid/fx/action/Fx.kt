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

typealias Handler = EventHandler<ActionEvent>

class Fx private constructor(
  val text: SimpleStringProperty = SimpleStringProperty(),
  val icon: SimpleStringProperty = SimpleStringProperty(),
  val description: SimpleStringProperty = SimpleStringProperty(),
  val accelerator: SimpleObjectProperty<KeyCombination> = SimpleObjectProperty(),
  val handler: SimpleObjectProperty<Handler> = SimpleObjectProperty(),
  val disabled: SimpleBooleanProperty = SimpleBooleanProperty(),
  val visible: SimpleBooleanProperty = SimpleBooleanProperty(true),
  val selected: SimpleBooleanProperty = SimpleBooleanProperty()
) {
  constructor(
    text: String? = null,
    icon: String? = null,
    description: String? = null,
    key: String? = null,
    handler: ((ActionEvent) -> Unit)? = null,
    disabled: ObservableValue<Boolean>? = null,
    visible: ObservableValue<Boolean>? = null,
    selected: Property<Boolean?>? = null
  ) : this(text = SimpleStringProperty()) {
    text?.also { this.text.bind(it.localized) }
    icon?.also { this.icon.bind(SimpleStringProperty(it)) }
    description?.also { this.description.bind(SimpleStringProperty(it)) }
    key?.also { this.accelerator.bind(SimpleObjectProperty(keyCombination(it))) }
    handler?.also { this.handler.bind(SimpleObjectProperty(EventHandler(it))) }
    disabled?.also { this.disabled.bind(it) }
    visible?.also { this.visible.bind(it) }
    selected?.also { this.selected.bindBidirectional(it) }
  }

  private val components get() = sequenceOf(text, icon, description, accelerator, handler, selected).map { it.value }

  override fun hashCode() = components.toList().hashCode()
  override fun equals(other: Any?) = when {
    other === this -> true
    other is Fx -> components.zip(other.components).all { (a, b) -> a == b }
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

  val isEmpty get() = sequenceOf(text, icon).all { it.value == null }

  operator fun invoke() {
    handler.get()?.handle(ActionEvent())
  }
}