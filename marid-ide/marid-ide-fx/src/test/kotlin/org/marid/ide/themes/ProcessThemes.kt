package org.marid.ide.themes

import javafx.css.Stylesheet
import java.lang.Thread.currentThread
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import kotlin.text.RegexOption.DOT_MATCHES_ALL
import kotlin.text.RegexOption.MULTILINE

object ProcessThemes {

  private val cssFiles = listOf(
    "com/sun/javafx/scene/control/skin/modena/modena.css",
    "com/sun/javafx/scene/control/skin/caspian/caspian.css"
  )

  @JvmStatic fun main(args: Array<String>) {
    cssFiles.forEach { path ->
      val url = currentThread().contextClassLoader.getResources(path).toList().last()
      val text = url?.readText(UTF_8).orEmpty()

      val normalized = text
        .replace(Regex("/[*].+?[*]/", setOf(MULTILINE, DOT_MATCHES_ALL)), "")
        .lineSequence()
        .filter(String::isNotBlank)
        .map(String::trimEnd)
        .joinToString(separator = "\n")

      val ast = Regex("(?:\\n|^)([^{}]+)[{]([^{}]+)[}]\\n", setOf(MULTILINE, DOT_MATCHES_ALL))
        .findAll(normalized)
        .map { match ->
          val key = LinkedHashSet(
            match.groupValues[1].splitToSequence(',')
              .map(String::trim)
              .map { it.replace(Regex("\\s*+>\\s*+"), " > ") }
              .toMutableList()
          )
          val values = match.groupValues[2].splitToSequence(';')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .map { it.split(delimiters = *charArrayOf(':'), limit = 2) }
            .map { Pair(it[0].trim(), it[1].replace(Regex("\\s++"), " ").trim()) }
            .fold(LinkedHashMap<String, String>()) { acc, (k, v) -> acc.apply { put(k, v) } }
          Pair(key, values)
        }
        .fold(LinkedHashMap<LinkedHashSet<String>, LinkedHashMap<String, String>>()) { a, (k, v) ->
          a.apply {
            compute(k) { _, old ->
              old?.also { it.putAll(v) } ?: v
            }
          }
        }

      val file = Path.of(javaClass.protectionDomain.codeSource.location.toURI())
        .parent
        .parent
        .resolve("src")
        .resolve("main")
        .resolve("resources")
        .resolve(path)

      ast[setOf(".root")]!!.apply {
        this["-fx-base"] = "rgb(50, 50, 50)"
        this["-fx-background"] = "derive(-fx-base, -10%)"
        this["-fx-color"] = "rgb(40, 40, 40)"
        this["-fx-control-inner-background"] = "rgb(20, 20, 20)"
        this["-fx-control-inner-background-alt"] = "derive(-fx-control-inner-background, 5%)"
        this["-fx-light-text-color"] = "rgb(220, 220, 220)"
        this["-fx-mid-text-color"] = "rgb(100, 100, 100)"
        this["-fx-dark-text-color"] = "rgb(20, 20, 20)"
        this["-fx-accent"] = "rgb(0, 80, 100)"
        this["-fx-selection-bar-non-focused"] = "derive(-fx-base, 20%)"
        this["-fx-mark-highlight-color"] = "derive(-fx-base, 40%)"
      }

      val transformed = ast
        .map { (k, v) -> k.joinToString(",", "", " {") + v.map { (k, v) -> "$k: $v;" }.joinToString("\n", "\n", "\n}") }
        .joinToString("\n")

      Files.createDirectories(file.parent)
      Files.writeString(file, transformed, UTF_8)

      val bss = file.parent.resolve(file.fileName.toString().replace(".css", ".bss"))
      Stylesheet.convertToBinary(file.toFile(), bss.toFile())
    }
  }
}