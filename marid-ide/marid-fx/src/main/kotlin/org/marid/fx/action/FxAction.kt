package org.marid.fx.action

import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyCombination.keyCombination
import org.marid.fx.i18n.localized
import java.util.*

typealias Handler = EventHandler<ActionEvent>

class FxAction private constructor(
  val text: StringProperty = SimpleStringProperty(),
  val icon: StringProperty = SimpleStringProperty(),
  val description: StringProperty = SimpleStringProperty(),
  val accelerator: ObjectProperty<KeyCombination> = SimpleObjectProperty(),
  val handler: ObjectProperty<Handler> = SimpleObjectProperty(),
  val selected: BooleanProperty = SimpleBooleanProperty()
) {
  constructor(
    text: String? = null,
    icon: String? = null,
    description: String? = null,
    key: String? = null,
    handler: ((ActionEvent) -> Unit)? = null,
    selected: Property<Boolean?>? = null
  ) : this(
    SimpleStringProperty().also { if (text != null) it.bind(text.localized) },
    SimpleStringProperty().also { if (icon != null) it.bind(SimpleStringProperty(icon)) },
    SimpleStringProperty().also { if (description != null) it.bind(description.localized) },
    SimpleObjectProperty<KeyCombination>().also { if (key != null) it.bind(SimpleObjectProperty(keyCombination(key))) },
    SimpleObjectProperty<Handler>().also { if (handler != null) it.bind(SimpleObjectProperty(EventHandler(handler))) },
    SimpleBooleanProperty().also { if (selected != null) it.bindBidirectional(selected) }
  )

  operator fun component1(): String? = text.get()
  operator fun component2(): String? = icon.get()
  operator fun component3(): String? = description.get()
  operator fun component4(): KeyCombination? = accelerator.get()
  operator fun component5(): EventHandler<ActionEvent>? = handler.get()

  override fun hashCode(): Int = Objects.hash(component1(), component2(), component3(), component4(), component5())
  override fun equals(other: Any?): Boolean = when (other) {
    other === this -> true
    is FxAction -> {
      this.component1() == other.component1()
        && this.component2() == other.component2()
        && this.component3() == other.component3()
        && this.component4() == other.component4()
        && this.component5() == other.component5()
    }
    else -> false
  }

  fun text(text: String): FxAction = also { this.text.bind(text.localized) }
  fun exactText(text: String): FxAction = also { this.text.bind(SimpleStringProperty(text)) }
  fun text(text: ObservableValue<String>): FxAction = also { this.text.bind(text) }
  fun icon(resource: String): FxAction = also { this.icon.bind(SimpleStringProperty(resource)) }
  fun icon(resource: ObservableValue<String>) = also { this.icon.bind(resource) }
  fun description(text: String): FxAction = also { this.description.bind(text.localized) }
  fun descriptionText(text: String): FxAction = also { this.description.bind(SimpleStringProperty(text)) }
  fun description(text: ObservableValue<String>) = also { this.description.bind(text) }
  fun accelerator(keys: String) = also { this.accelerator.bind(SimpleObjectProperty(keyCombination(keys))) }
  fun accelerator(keys: KeyCombination) = also { this.accelerator.bind(SimpleObjectProperty(keys)) }
  fun accelerator(keys: ObservableValue<KeyCombination>) = also { this.accelerator.bind(keys) }
  fun handler(handler: EventHandler<ActionEvent>) = also { this.handler.bind(SimpleObjectProperty(handler)) }
  fun handler(handler: (ActionEvent) -> Unit) = also { this.handler.bind(SimpleObjectProperty(EventHandler(handler))) }
  fun handler(handler: ObservableValue<EventHandler<ActionEvent>>) = also { this.handler.bind(handler) }
  fun selected(selected: Property<Boolean?>) = also { this.selected.bindBidirectional(selected) }

}