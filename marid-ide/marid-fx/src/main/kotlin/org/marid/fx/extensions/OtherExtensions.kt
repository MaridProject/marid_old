package org.marid.fx.extensions

import com.sun.javafx.application.PlatformImpl
import javafx.application.Platform
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.atomic.AtomicReference

fun Properties.loadFromResource(resource: String): Properties {
  val loader = Thread.currentThread().contextClassLoader
  InputStreamReader(
    loader.getResourceAsStream(resource)!!,
    StandardCharsets.UTF_8
  ).use {
    load(it)
  }
  return this
}

fun <T, R> T.callFx(f: (T) -> R?): R? {
  if (Platform.isFxApplicationThread()) {
    return f(this)
  } else {
    val resultRef = AtomicReference<Pair<R?, Throwable?>>()
    PlatformImpl.runAndWait {
      try {
        val result = f(this)
        resultRef.set(Pair(result, null))
      } catch (e: Throwable) {
        resultRef.set(Pair(null, e))
      }
    }
    val error = resultRef.get().second
    val result = resultRef.get().first
    if (error != null) {
      throw error
    } else {
      return result
    }
  }
}

fun <T> T.runFx(f: (T) -> Unit): Unit {
  if (Platform.isFxApplicationThread()) {
    f(this)
  } else {
    Platform.runLater { f(this) }
  }
}