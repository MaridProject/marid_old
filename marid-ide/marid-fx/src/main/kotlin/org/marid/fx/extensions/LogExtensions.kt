package org.marid.fx.extensions

import javafx.scene.paint.Color
import org.marid.fx.i18n.I18n
import java.text.MessageFormat
import java.util.*
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

inline val Level.color: Color
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

inline val Any.logger: Logger get() = Logger.getLogger(javaClass.name)

fun Logger.LOG(level: Level, msg: String?, exception: Throwable?, vararg args: Any?) =
  log(LogRecord(level, msg).apply {
    loggerName = this@LOG.name
    thrown = exception
    parameters = args
    sourceClassName = null
    sourceMethodName = null
  })

fun Logger.LOG(level: Level, msg: String?, vararg args: Any?) =
  log(LogRecord(level, msg).apply {
    loggerName = this@LOG.name
    parameters = args
    sourceClassName = null
    sourceMethodName = null
  })


fun Logger.FINEST(msg: String?, exception: Throwable?, vararg args: Any?) = LOG(Level.FINEST, msg, exception, args)
fun Logger.FINER(msg: String?, exception: Throwable?, vararg args: Any?) = LOG(Level.FINER, msg, exception, args)
fun Logger.FINE(msg: String?, exception: Throwable?, vararg args: Any?) = LOG(Level.FINE, msg, exception, args)
fun Logger.CONFIG(msg: String?, exception: Throwable?, vararg args: Any?) = LOG(Level.CONFIG, msg, exception, args)
fun Logger.INFO(msg: String?, exception: Throwable?, vararg args: Any?) = LOG(Level.INFO, msg, exception, args)
fun Logger.WARN(msg: String?, exception: Throwable?, vararg args: Any?) = LOG(Level.WARNING, msg, exception, args)
fun Logger.ERROR(msg: String?, exception: Throwable?, vararg args: Any?) = LOG(Level.SEVERE, msg, exception, args)

fun Logger.FINEST(msg: String?, vararg args: Any?) = log(Level.FINEST, msg, args)
fun Logger.FINER(msg: String?, vararg args: Any?) = log(Level.FINER, msg, args)
fun Logger.FINE(msg: String?, vararg args: Any?) = log(Level.FINE, msg, args)
fun Logger.CONFIG(msg: String?, vararg args: Any?) = log(Level.CONFIG, msg, args)
fun Logger.INFO(msg: String?, vararg args: Any?) = log(Level.INFO, msg, args)
fun Logger.WARN(msg: String?, vararg args: Any?) = log(Level.WARNING, msg, args)
fun Logger.ERROR(msg: String?, vararg args: Any?) = log(Level.SEVERE, msg, args)

val LogRecord.formatSafe: String get() {
  try {
    val bundle = I18n.textsBundle()
    val msg = try {
      bundle.getString(message)
    } catch (e: MissingResourceException) {
      message
    }
    if (parameters.isNullOrEmpty()) {
      return msg
    } else {
      return MessageFormat.format(msg, *parameters)
    }
  } catch (e: Throwable) {
    return message
  }
}