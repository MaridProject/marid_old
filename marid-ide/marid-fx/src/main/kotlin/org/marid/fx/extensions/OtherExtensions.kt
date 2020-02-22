package org.marid.fx.extensions

import javafx.application.Platform
import java.io.InputStreamReader
import java.lang.ref.Cleaner
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture

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

fun <T, R> T.callFx(f: (T) -> R): R =
  if (Platform.isFxApplicationThread()) {
    f(this)
  } else {
    val future = CompletableFuture<R>()
    Platform.runLater {
      try {
        future.complete(f(this))
      } catch (e: Throwable) {
        future.completeExceptionally(e)
      }
    }
    future.get()
  }

fun <T> T.runFx(f: (T) -> Unit): Unit {
  if (Platform.isFxApplicationThread()) {
    f(this)
  } else {
    Platform.runLater { f(this) }
  }
}

val FX_CLEANER = Cleaner.create()