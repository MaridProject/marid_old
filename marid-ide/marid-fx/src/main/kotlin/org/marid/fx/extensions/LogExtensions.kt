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


fun Logger.lft(msg: String, exception: Throwable, vararg args: Any) = log(Level.FINEST, msg, exception, args)
fun Logger.lfr(msg: String, exception: Throwable, vararg args: Any) = log(Level.FINER, msg, exception, args)
fun Logger.lfn(msg: String, exception: Throwable, vararg args: Any) = log(Level.FINE, msg, exception, args)
fun Logger.lfc(msg: String, exception: Throwable, vararg args: Any) = log(Level.CONFIG, msg, exception, args)
fun Logger.lif(msg: String, exception: Throwable, vararg args: Any) = log(Level.INFO, msg, exception, args)
fun Logger.lwn(msg: String, exception: Throwable, vararg args: Any) = log(Level.WARNING, msg, exception, args)
fun Logger.ler(msg: String, exception: Throwable, vararg args: Any) = log(Level.SEVERE, msg, exception, args)

fun Logger.lft(msg: String, vararg args: Any) = log(Level.FINEST, msg, args)
fun Logger.lfr(msg: String, vararg args: Any) = log(Level.FINER, msg, args)
fun Logger.lfn(msg: String, vararg args: Any) = log(Level.FINE, msg, args)
fun Logger.lfc(msg: String, vararg args: Any) = log(Level.CONFIG, msg, args)
fun Logger.lif(msg: String, vararg args: Any) = log(Level.INFO, msg, args)
fun Logger.lwn(msg: String, vararg args: Any) = log(Level.WARNING, msg, args)
fun Logger.ler(msg: String, vararg args: Any) = log(Level.SEVERE, msg, args)
