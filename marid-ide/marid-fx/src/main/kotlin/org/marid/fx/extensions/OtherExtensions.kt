package org.marid.fx.extensions

import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

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