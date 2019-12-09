package org.marid.ide.themes

import javafx.css.Stylesheet
import java.lang.Thread.currentThread
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path

object ProcessThemes {
  @JvmStatic fun main(args: Array<String>) {
    listOf("com/sun/javafx/scene/control/skin/modena/modena.css").forEach { path ->
      val url = currentThread().contextClassLoader.getResource(path)
      val text = url?.readText(UTF_8).orEmpty()

      val file = Path.of(javaClass.protectionDomain.codeSource.location.toURI())
        .parent
        .parent
        .resolve("src")
        .resolve("main")
        .resolve("resources")
        .resolve(path)

      val lines = ArrayList(text.lines())
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

        if (trimmed.startsWith("-fx-") && trimmed.endsWith(";")) {
          val parts = trimmed.split(':').map { it.trim() }
          when (parts[0]) {
            "-fx-base" ->
              lines[i] = line.replace(parts[1], "#121212;")
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