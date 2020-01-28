package org.marid.ide.common

import org.marid.fx.extensions.loadFromResource
import org.springframework.stereotype.Component
import java.util.*

@Component
class IdeProperties {

  private val meta = Properties().loadFromResource("marid/meta.properties")

  val projectVersion = meta.getProperty("implementation.version")

  fun substitute(string: String): String = listOf(meta)
    .fold(string) { s, p ->
      p.stringPropertyNames().fold(s) { e, k -> e.replace("\${$k}", p.getProperty(k)) }
    }
}