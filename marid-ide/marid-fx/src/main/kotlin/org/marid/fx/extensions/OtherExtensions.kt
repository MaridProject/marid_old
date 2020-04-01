/*-
 * #%L
 * marid-fx
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
