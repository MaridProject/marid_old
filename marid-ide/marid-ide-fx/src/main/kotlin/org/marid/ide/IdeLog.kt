package org.marid.ide

import javafx.application.Platform
import javafx.collections.FXCollections
import org.marid.ide.extensions.pref
import org.marid.spring.annotation.InternalComponent
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@InternalComponent
class IdeLog {

  private val maxLogRecords = pref("maxLogRecords", 10_000)
  private val logList = FXCollections.observableArrayList<LogRecord>()
  private val logHandler = object : Handler() {
    override fun publish(record: LogRecord) {
      if (Platform.isFxApplicationThread()) {
        val maxLogRecords = this@IdeLog.maxLogRecords.get()
        logList.add(record)
        while (logList.size > maxLogRecords) {
          logList.removeAt(0)
        }
      } else Platform.runLater {
        val maxLogRecords = this@IdeLog.maxLogRecords.get()
        logList.add(record)
        while (logList.size > maxLogRecords) {
          logList.removeAt(0)
        }
      }
    }

    override fun flush() {
    }

    override fun close() {
    }
  }

  @PostConstruct
  private fun init() {
    Logger.getLogger("").addHandler(logHandler)
  }

  @PreDestroy
  private fun destroy() {
    Logger.getLogger("").removeHandler(logHandler)
  }
}