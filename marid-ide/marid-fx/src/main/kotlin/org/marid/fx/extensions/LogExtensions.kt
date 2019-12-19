package org.marid.fx.extensions

import javafx.scene.paint.Color
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

fun Logger.log(level: Level, msg: String, exception: Throwable, vararg args: Any) =
  logger.log(LogRecord(level, msg).apply {
    loggerName = logger.name
    thrown = exception
    parameters = args
    sourceClassName = null
    sourceMethodName = null
  })

fun Logger.log(level: Level, msg: String, vararg args: Any) =
  logger.log(LogRecord(level, msg).apply {
    loggerName = logger.name
    parameters = args
    sourceClassName = null
    sourceMethodName = null
  })


fun Logger.finest(msg: String, exception: Throwable, vararg args: Any) = log(Level.FINEST, msg, exception, args)
fun Logger.finer(msg: String, exception: Throwable, vararg args: Any) = log(Level.FINER, msg, exception, args)
fun Logger.fine(msg: String, exception: Throwable, vararg args: Any) = log(Level.FINE, msg, exception, args)
fun Logger.config(msg: String, exception: Throwable, vararg args: Any) = log(Level.CONFIG, msg, exception, args)
fun Logger.info(msg: String, exception: Throwable, vararg args: Any) = log(Level.INFO, msg, exception, args)
fun Logger.warn(msg: String, exception: Throwable, vararg args: Any) = log(Level.WARNING, msg, exception, args)
fun Logger.error(msg: String, exception: Throwable, vararg args: Any) = log(Level.SEVERE, msg, exception, args)

fun Logger.finest(msg: String, vararg args: Any) = log(Level.FINEST, msg, args)
fun Logger.finer(msg: String, vararg args: Any) = log(Level.FINER, msg, args)
fun Logger.fine(msg: String, vararg args: Any) = log(Level.FINE, msg, args)
fun Logger.config(msg: String, vararg args: Any) = log(Level.CONFIG, msg, args)
fun Logger.info(msg: String, vararg args: Any) = log(Level.INFO, msg, args)
fun Logger.warn(msg: String, vararg args: Any) = log(Level.WARNING, msg, args)
fun Logger.error(msg: String, vararg args: Any) = log(Level.SEVERE, msg, args)
