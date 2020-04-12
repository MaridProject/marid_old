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

package org.marid.fx.validation

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import javafx.geometry.Insets
import javafx.scene.AccessibleAttribute
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Tooltip
import javafx.scene.effect.Shadow
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle.SOLID
import javafx.scene.layout.BorderWidths
import org.marid.fx.extensions.color
import org.marid.fx.extensions.map
import org.marid.fx.extensions.mapBoolean
import org.marid.fx.extensions.mapString
import java.util.*
import java.util.logging.Level

class Validation {

  private val validators = IdentityHashMap<Node, LinkedList<ObservableValue<ValidationResult>>>()
  private val worstLevelProperty = ReadOnlyObjectWrapper(Level.ALL)

  private fun add0(node: Node, result: ObservableValue<ValidationResult>) {
    worstLevelProperty.set(listOf(worstLevelProperty.get(), result.value.level).maxBy { it.intValue() })
    validators.computeIfAbsent(node) { LinkedList() }.add(result)
    val tooltip = Tooltip()
    tooltip.textProperty().bind(result.mapString { it.message })
    val listener = ChangeListener<ValidationResult> { _, _, v ->
      worstLevelProperty.set(validators.values.flatten().map { it.value.level }.maxBy { it.intValue() } ?: Level.ALL)
      if (v.level.intValue() != Level.ALL.intValue()) {
        when (node) {
          is Control -> node.border = Border(BorderStroke(
            null,
            null,
            v.level.color,
            null,
            null,
            null,
            SOLID,
            null,
            null,
            BorderWidths(3.0),
            Insets(1.0)))
          else -> node.effect = Shadow(3.0, v.level.color)
        }
        Tooltip.install(node, tooltip)
      } else {
        when (node) {
          is Control -> node.border = null
          else -> node.effect = null
        }
        Tooltip.uninstall(node, tooltip)
      }
    }
    val weakListener = WeakChangeListener(listener)
    node.properties[listenerKey] = listener
    node.properties[weakListenerKey] = weakListener
    result.addListener(weakListener)
    listener.changed(result, null, result.value)
  }

  fun add(node: Node, result: ObservableValue<ValidationResult>) {
    node.sceneProperty().addListener { _, _, scene ->
      if (scene != null) {
        val labeledBy = node.queryAccessibleAttribute(AccessibleAttribute.LABELED_BY) as Node?
        val target = labeledBy ?: node
        add0(target, result)
      }
    }
  }

  fun worstLevelProperty(): ReadOnlyProperty<Level> = worstLevelProperty.readOnlyProperty
  val valid = worstLevelProperty.mapBoolean { it.intValue() < Level.SEVERE.intValue() }
  val invalid = worstLevelProperty.mapBoolean { it.intValue() >= Level.SEVERE.intValue() }

  private companion object {
    private val listenerKey = Object()
    private val weakListenerKey = Object()
  }
}

fun <T> ObservableValue<T>.validate(predicate: (T) -> Boolean, error: (T) -> String): ObjectBinding<ValidationResult> =
  map { if (predicate(it)) ValidationResult(error(it)) else ValidationResult() }
