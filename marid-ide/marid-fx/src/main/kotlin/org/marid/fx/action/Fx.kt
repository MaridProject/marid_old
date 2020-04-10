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

import com.sun.javafx.binding.ObjectConstant
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyCombination.keyCombination
import org.marid.fx.i18n.localized
import kotlin.reflect.full.declaredMemberProperties

typealias Handler = EventHandler<ActionEvent>

class Fx(
  text: String? = null,
  icon: String? = null,
  description: String? = null,
  key: String? = null,
  h: ((ActionEvent) -> Unit)? = null,
  disabled: ObservableValue<Boolean>? = null,
  visible: ObservableValue<Boolean>? = null,
  selected: Property<Boolean?>? = null
) {
  private val text0 = ReadOnlyStringWrapper()
  private val icon0 = ReadOnlyStringWrapper()
  private val description0 = ReadOnlyStringWrapper()
  private val accelerator0 = ReadOnlyObjectWrapper<KeyCombination?>()
  private val handler0 = ReadOnlyObjectWrapper<Handler?>()
  private val disabled0 = ReadOnlyBooleanWrapper()
  private val visible0 = ReadOnlyBooleanWrapper(true)
  private val selected0 = ReadOnlyBooleanWrapper().also { it.bind(ObjectConstant.valueOf(true)) }

  init {
    text?.also { text0.bind(it.localized) }
    icon?.also { icon0.bind(SimpleStringProperty(it)) }
    description?.also { description0.bind(SimpleStringProperty(it)) }
    key?.also { accelerator0.bind(SimpleObjectProperty(keyCombination(it))) }
    h?.also { hdl -> handler0.bind(SimpleObjectProperty(EventHandler { hdl(it); it.consume() })) }
    disabled?.also { disabled0.bind(it) }
    visible?.also { visible0.bind(it) }
    selected?.also { selected0.unbind(); selected0.bindBidirectional(it) }
  }

  val text: ReadOnlyStringProperty get() = text0.readOnlyProperty
  val icon: ReadOnlyStringProperty get() = icon0.readOnlyProperty
  val description: ReadOnlyStringProperty get() = description0.readOnlyProperty
  val accelerator: ReadOnlyObjectProperty<KeyCombination?> = accelerator0.readOnlyProperty
  val handler: ReadOnlyObjectProperty<Handler?> = handler0.readOnlyProperty
  val disabled: ReadOnlyBooleanProperty = disabled0.readOnlyProperty
  val visible: ReadOnlyBooleanProperty = visible0.readOnlyProperty
  val selected: ReadOnlyBooleanProperty = selected0.readOnlyProperty

  private val props
    get() = Fx::class.declaredMemberProperties
      .flatMap {
        when {
          ObservableValue::class.java.isAssignableFrom(it.returnType.javaClass) ->
            listOf((it.get(this) as ObservableValue<*>).value)
          ObservableList::class.java.isAssignableFrom(it.returnType.javaClass) ->
            listOf(it.get(this))
          else -> emptyList()
        }
      }

  override fun hashCode() = props.hashCode()
  override fun equals(other: Any?) = when {
    other === this -> true
    other is Fx -> props == other.props
    else -> false
  }

  fun text(text: String) = also { text0.bind(text.localized) }
  fun exactText(text: String) = also { text0.bind(SimpleStringProperty(text)) }
  fun text(text: ObservableValue<String>) = also { text0.bind(text) }
  fun icon(resource: String) = also { icon0.bind(SimpleStringProperty(resource)) }
  fun icon(resource: ObservableValue<String>) = also { icon0.bind(resource) }
  fun description(text: String) = also { description0.bind(text.localized) }
  fun descriptionText(text: String) = also { description0.bind(SimpleStringProperty(text)) }
  fun description(text: ObservableValue<String>) = also { description0.bind(text) }
  fun accelerator(keys: String) = also { accelerator0.bind(SimpleObjectProperty(keyCombination(keys))) }
  fun accelerator(keys: KeyCombination) = also { accelerator0.bind(SimpleObjectProperty(keys)) }
  fun accelerator(keys: ObservableValue<KeyCombination>) = also { accelerator0.bind(keys) }
  fun handler(handler: EventHandler<ActionEvent>) = also { handler0.bind(SimpleObjectProperty(handler)) }
  fun handler(handler: (ActionEvent) -> Unit) = also { handler0.bind(SimpleObjectProperty(EventHandler(handler))) }
  fun handler(handler: ObservableValue<EventHandler<ActionEvent>>) = also { handler0.bind(handler) }
  fun selected(selected: Property<Boolean?>) = also { selected.unbind(); selected0.bindBidirectional(selected) }

  fun linkSelected(selected: Property<Boolean?>) = selected.bindBidirectional(selected0)

  val isEmpty get() = sequenceOf(text0, icon0).all { it.value == null }
  val selectedBound get() = !selected0.isBound

  operator fun invoke() {
    handler0.get()?.handle(ActionEvent())
  }
}
