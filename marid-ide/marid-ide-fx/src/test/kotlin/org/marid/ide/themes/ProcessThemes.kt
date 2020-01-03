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
          val key = LinkedHashSet(match.groupValues[1].splitToSequence(',').map(String::trim).toMutableList())
          val values = match.groupValues[2].splitToSequence(';')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .map { it.split(delimiters = *charArrayOf(':'), limit = 2) }
            .map { Pair(it[0].trim(), it[1].replace(Regex("\\s++"), " ").trim()) }
            .fold(LinkedHashMap<String, String>()) { acc, (k, v) -> acc[k] = v; acc }
          Pair(key, values)
        }
        .fold(LinkedHashMap<LinkedHashSet<String>, LinkedHashMap<String, String>>()) { a, (k, v) -> a[k] = v; a }

      val file = Path.of(javaClass.protectionDomain.codeSource.location.toURI())
        .parent
        .parent
        .resolve("src")
        .resolve("main")
        .resolve("resources")
        .resolve(path)

      ast[setOf(".root")]?.apply {
        this["-fx-base"] = "#121212"
        this["-fx-background"] = "derive(-fx-base,26.4%)"
        this["-fx-control-inner-background"] = "derive(-fx-base,80%)"
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