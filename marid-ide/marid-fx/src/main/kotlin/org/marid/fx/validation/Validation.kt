package org.marid.fx.validation

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
import org.marid.fx.extensions.bindBoolean
import org.marid.fx.extensions.bindString
import org.marid.fx.extensions.color
import java.util.*
import java.util.logging.Level

class Validation {

  private val validators = IdentityHashMap<Node, LinkedList<ObservableValue<ValidationResult>>>()
  private val worstLevelProperty = ReadOnlyObjectWrapper(Level.ALL)

  fun add(node: Node, result: ObservableValue<ValidationResult>) {
    worstLevelProperty.set(listOf(worstLevelProperty.get(), result.value.level).maxBy { it.intValue() })
    validators.computeIfAbsent(node) { LinkedList() }.add(result)
    val tooltip = Tooltip()
    tooltip.textProperty().bind(result.bindString { it.value.message })
    val listener = ChangeListener<ValidationResult> { _, _, v ->
      worstLevelProperty.set(validators.values.flatten().map { it.value.level }.maxBy { it.intValue() } ?: Level.ALL)
      if (v.level.intValue() != Level.ALL.intValue()) {
        when (node) {
          is Control -> node.border = Border(BorderStroke(v.level.color, SOLID, null, BorderWidths(2.0), Insets(3.0)))
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
  }

  fun addToLabel(node: Node, result: ObservableValue<ValidationResult>) {
    node.sceneProperty().addListener { _, _, scene ->
      if (scene != null) {
        val labeledBy = node.queryAccessibleAttribute(AccessibleAttribute.LABELED_BY) as Node?
        val target = labeledBy ?: node
        add(target, result)
      }
    }
  }

  fun worstLevelProperty(): ReadOnlyProperty<Level> = worstLevelProperty.readOnlyProperty
  val valid = worstLevelProperty.bindBoolean { it.get().intValue() < Level.SEVERE.intValue() }
  val invalid = worstLevelProperty.bindBoolean { it.get().intValue() >= Level.SEVERE.intValue() }

  private companion object {
    private val listenerKey = Object()
    private val weakListenerKey = Object()
  }
}