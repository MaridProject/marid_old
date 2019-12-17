package org.marid.fx.extensions

import javafx.scene.paint.Color
import org.intellij.lang.annotations.MagicConstant
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

val Level.color: Color
  get() = when (intValue()) {
    Level.OFF.intValue() -> Color.DARKGRAY
    Level.FINEST.intValue() -> Color.LIGHTGREEN
    Level.FINER.intValue() -> Color.GREEN
    Level.FINE.intValue() -> Color.DARKGREEN
    Level.CONFIG.intValue() -> Color.DARKCYAN
    Level.INFO.intValue() -> Color.LIGHTBLUE
    Level.WARNING.intValue() -> Color.ORANGE
    Level.SEVERE.intValue() -> Color.ORANGERED
    Level.ALL.intValue() -> Color.GRAY
    else -> Color.BLACK
  }

fun Any.log(@MagicConstant(valuesFromClass = Level::class) level: Level, msg: String, ex: Throwable, vararg args: Any) {
  val logger = Logger.getLogger(javaClass.name)
  logger.log(LogRecord(level, msg).apply {
    loggerName = logger.name
    thrown = ex
    parameters = args
    sourceClassName = null
    sourceMethodName = null
  })
}

fun Any.log(@MagicConstant(valuesFromClass = Level::class) level: Level, msg: String, vararg args: Any) {
  val logger = Logger.getLogger(javaClass.name)
  logger.log(LogRecord(level, msg).apply {
    loggerName = logger.name
    parameters = args
    sourceClassName = null
    sourceMethodName = null
  })
}