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
        .replace(Regex("[ ]++"), " ")
        .lineSequence()
        .filter(String::isNotBlank)
        .map(String::trimEnd)
        .joinToString(separator = "\n")
        .replace(Regex("[(][^;]+[)];")) {it.value.replace(Regex("\\s++"), " ").trim()}
        .replace(Regex("[\\n^]([^{}]+)[{]([^{}]+)[}]")) {"\n" + it.groupValues[1].trim().replace(Regex("\\s++"), " ") + " {" + it.groupValues[2] + "}"}
      println(normalized)

      val file = Path.of(javaClass.protectionDomain.codeSource.location.toURI())
        .parent
        .parent
        .resolve("src")
        .resolve("main")
        .resolve("resources")
        .resolve(path)

      val lines = ArrayList(normalized.lines())
      var replaceEnable = false
      for (i in lines.indices) {
        val line = lines[i]
        val trimmed = line.trim()

        if (!replaceEnable && trimmed == ".root {") {
          replaceEnable = true
        }

        if (replaceEnable && trimmed == "}") {
          break
        }

        val passed = HashSet<String>()
        if (trimmed.startsWith("-fx-") && trimmed.endsWith(";")) {
          val parts = trimmed.split(':').map { it.trim() }
          if (passed.add(parts[0])) {
            when (parts[0]) {
              "-fx-base" -> lines[i] = line.replace(parts[1], "#121212;")
              "-fx-background" -> lines[i] = line.replace(parts[1], "derive(-fx-base,26.4%);")
              "-fx-control-inner-background" -> lines[i] = line.replace(parts[1], "derive(-fx-base,80%);")
            }
          }
        }
      }

      Files.createDirectories(file.parent)
      Files.write(file, lines, UTF_8)

      val bss = file.parent.resolve(file.fileName.toString().replace(".css", ".bss"))
      Stylesheet.convertToBinary(file.toFile(), bss.toFile())
    }
  }
}