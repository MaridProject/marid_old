package org.marid.fx.i18n

import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableValue
import java.util.concurrent.Callable

val String.localized: StringBinding
  get() = Bindings.createStringBinding(Callable {
    val bundle = I18n.textsBundle()
    try {
      bundle.getString(this)
    } catch (e: Throwable) {
      this
    }
  }, I18n.locale)

fun String.localized(vararg args: Any?): StringBinding {
  val observables = args.flatMap { if (it is Observable) listOf(it) else emptyList() }.toTypedArray()
  return Bindings.createStringBinding(Callable { i18n(*args) }, *observables, I18n.locale)
}

fun String.i18n(vararg args: Any?): String {
  val bundle = I18n.textsBundle()
  val format = try {
    bundle.getString(this)
  } catch (e: Throwable) {
    this
  }
  return if (args.isEmpty()) {
    format
  } else {
    try {
      val extractedArgs = args.map { if (it is ObservableValue<*>) it.value else it }.toTypedArray()
      String.format(format, *extractedArgs)
    } catch (e: Throwable) {
      format
    }
  }
}