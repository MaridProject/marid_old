package org.marid.fx.dialog

import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import org.marid.fx.action.Fx
import org.marid.fx.i18n.localized

@Target(AnnotationTarget.PROPERTY)
@Retention
annotation class FxDialogProp(val label: String, val icon: String) {
  companion object {
    val FxDialogProp.fx
      get() = Fx(
        text = label.takeUnless { it.isBlank() },
        icon = icon.takeUnless { it.isBlank() }
      )
  }
}

abstract class FxDialogData(title: ObservableValue<String>) {

  val title = SimpleStringProperty()

  init {
    this.title.bind(title)
  }

  constructor(title: String): this(title.localized)
}