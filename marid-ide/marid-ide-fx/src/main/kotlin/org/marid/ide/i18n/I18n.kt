package org.marid.ide.i18n

import org.marid.fx.extensions.pref
import java.lang.Thread.currentThread
import java.util.*
import java.util.ResourceBundle.Control.FORMAT_PROPERTIES
import java.util.ResourceBundle.Control.getControl
import java.util.ResourceBundle.getBundle

object I18n {

  private val control = getControl(FORMAT_PROPERTIES)
  internal fun textsBundle() = getBundle("texts", locale.get(), currentThread().contextClassLoader, control)

  val locale = pref("locale", Locale.getDefault())
}