package org.marid.fx.validation

import javafx.beans.value.ObservableBooleanValue
import org.marid.fx.extensions.bindObject
import java.util.logging.Level

data class ValidationResult(val level: Level, val message: String) {
  constructor() : this(Level.ALL, "")
  constructor(message: String) : this(Level.SEVERE, message)
}

fun ObservableBooleanValue.validate(message: String, level: Level = Level.SEVERE) =
  bindObject { if (get()) ValidationResult(level, message) else ValidationResult() }