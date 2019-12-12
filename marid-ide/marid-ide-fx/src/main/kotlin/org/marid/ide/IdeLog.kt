package org.marid.ide

import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.FXCollections
import org.marid.ide.extensions.pref
import org.marid.spring.annotation.InternalComponent
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import javax.annotation.PreDestroy

@InternalComponent
class IdeLog internal constructor() {

  private val maxLogRecordsPref = pref("maxLogRecords", 10_000)
  private val logList = FXCollections.observableArrayList<LogRecord>()
  private val lastLevelProperty = ReadOnlyObjectWrapper<Level>(this, "lastLevel", Level.ALL)
  private val logHandler = object : Handler() {
    override fun publish(record: LogRecord) {
      if (Platform.isFxApplicationThread()) {
        add(record)
      } else Platform.runLater {
        add(record)
      }
    }

    override fun flush() {
    }

    override fun close() {
    }
  }

  init {
    rootLogger().addHandler(logHandler)
  }

  private fun add(record: LogRecord) {
    val maxLogRecords = this@IdeLog.maxLogRecordsPref.get()
    logList.add(record)
    while (logList.size > maxLogRecords) {
      logList.removeAt(0)
    }
    if (record.level.intValue() > lastLevelProperty.get().intValue()) {
      lastLevelProperty.set(record.level)
    }
  }

  @PreDestroy
  private fun destroy() {
    rootLogger().removeHandler(logHandler)
  }

  fun reset() = lastLevelProperty.set(Level.ALL)
  val lastLevel: ReadOnlyObjectProperty<Level>
    get() = lastLevelProperty.readOnlyProperty
  var maxLogRecords: Int
    get() = maxLogRecordsPref.get()
    set(value) = maxLogRecordsPref.set(value)

  companion object {
    internal fun rootLogger() = Logger.getLogger("")
  }
}