package org.marid.ide.log

import javafx.application.Platform
import javafx.beans.property.ReadOnlyBooleanWrapper
import org.apache.ivy.util.AbstractMessageLogger
import org.apache.ivy.util.Message
import org.marid.fx.extensions.*
import java.util.logging.Logger

class IdeMessageLogger(private val logger: Logger) : AbstractMessageLogger() {

  private val progressWrapper = ReadOnlyBooleanWrapper()

  val progress = progressWrapper.readOnlyProperty

  init {
    isShowProgress = true
  }

  override fun rawlog(msg: String?, level: Int) = log(msg, level)
  override fun doProgress() = Platform.runLater { progressWrapper.set(true) }
  override fun doEndProgress(msg: String?) = Platform.runLater { progressWrapper.set(false) }

  override fun log(msg: String?, level: Int) = when (level) {
    Message.MSG_DEBUG -> logger.fnr("{0}", msg)
    Message.MSG_VERBOSE -> logger.fin("{0}", msg)
    Message.MSG_INFO -> logger.inf("{0}", msg)
    Message.MSG_WARN -> logger.wrn("{0}", msg)
    Message.MSG_ERR -> logger.err("{0}", msg)
    else -> logger.cfg("{0}", msg)
  }
}