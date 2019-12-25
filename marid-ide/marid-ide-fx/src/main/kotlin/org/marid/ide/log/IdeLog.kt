package org.marid.ide.log

import javafx.application.Platform.isFxApplicationThread
import javafx.application.Platform.runLater
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.marid.fx.extensions.pref
import org.marid.spring.annotation.InjectedComponent
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import javax.annotation.PreDestroy
import kotlin.math.max

@InjectedComponent
class IdeLog internal constructor() {

  private val maxLogRecordsPref = pref("maxLogRecords", 10_000)
  private val recordList = FXCollections.observableArrayList<LogRecord>()
  private val lastLevelProperty = ReadOnlyObjectWrapper<Level>(this, "lastLevel", Level.ALL)
  private val logHandler = object : Handler() {
    override fun publish(record: LogRecord) = if (isFxApplicationThread()) add(record) else runLater {add(record)}
    override fun flush() {}
    override fun close() {}
  }

  init {
    rootLogger().addHandler(logHandler)
  }

  private fun add(record: LogRecord) {
    val maxLogRecords = max(this@IdeLog.maxLogRecordsPref.get(), 10)
    recordList.add(record)
    while (recordList.size > maxLogRecords) {
      recordList.removeAt(0)
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
  val lastLevel: ReadOnlyObjectProperty<Level> get() = lastLevelProperty.readOnlyProperty
  var maxLogRecords: Int get() = maxLogRecordsPref.get(); set(value) = maxLogRecordsPref.set(value)
  val records: ObservableList<LogRecord> get() = FXCollections.unmodifiableObservableList(recordList)

  companion object {
    internal fun rootLogger() = Logger.getLogger("")
  }
}