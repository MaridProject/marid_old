package org.marid.ide.extensions

import org.marid.ide.Ide
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

object GenerateExtensions {

  private val numericTypes = listOf("Double", "Float", "Long", "Int")
  private val types = numericTypes + listOf("String", "Boolean")
  private val cmpTypes = numericTypes + listOf("String")

  private val numericOps = linkedMapOf(
    "minus" to "subtract",
    "plus" to "add",
    "times" to "multiply",
    "div" to "divide"
  )

  @JvmStatic fun main(args: Array<String>) {
    val curDir = Path.of(javaClass.protectionDomain.codeSource.location.toURI())
    val kotlinDir = curDir.parent.parent.resolve("src").resolve("main").resolve("kotlin")
    val ideDir = Ide.javaClass.packageName.split('.').fold(kotlinDir, { acc, e -> acc.resolve(e) })
    val extensionsDir = ideDir.resolve("extensions")

    Files.createDirectories(extensionsDir)

    gen(extensionsDir.resolve("BindingExtensions.kt")) { file ->
      file.format("fun <T, R> ObservableValue<T>.map(func: (T) -> R): ObjectBinding<R> = createObjectBinding(java.util.concurrent.Callable { func(value)}, this)%n")
      for (p in types) {
        val t = mt(p)
        file.format("fun <T> ObservableValue<T>.map${p}(func: (T) -> $p): ${t}Binding = create${t}Binding(java.util.concurrent.Callable { func(value) }, this)%n")
        for (pn in types) {
          if (pn == p) {
            file.format("fun Observable${t}Value.map(func: ($p) -> $p): ${t}Binding = create${t}Binding(java.util.concurrent.Callable { func(get()) }, this)%n")
          } else {
            val tn = mt(pn)
            file.format("fun Observable${t}Value.map${pn}(func: ($p) -> $pn): ${tn}Binding = create${tn}Binding(java.util.concurrent.Callable { func(get()) }, this)%n")
          }
        }
      }
    }

    gen(extensionsDir.resolve("BindingNumericOpsExtensions.kt")) { file ->
      for ((op, method) in numericOps) {
        file.format("// %s --> %s%n", op, method)
        for (p in numericTypes) {
          for (pn in numericTypes) {
            val rt = mt(nt(p, pn))
            file.format("fun Observable${mt(p)}Value.$op(v: $pn): ${rt}Binding = $method(this, v) as ${rt}Binding%n")
            file.format("fun $p.$op(v: Observable${mt(pn)}Value): ${rt}Binding = $method(this, v) as ${rt}Binding%n")
            file.format("fun Observable${mt(p)}Value.$op(v: Observable${mt(pn)}Value): ${rt}Binding = $method(this, v) as ${rt}Binding%n")
          }
        }
        file.println()
      }
    }
  }

  private fun gen(path: Path, code: (PrintWriter) -> Unit) {
    PrintWriter(path.toFile(), StandardCharsets.UTF_8).use { file ->
      file.format("package %s.%s%n", Ide.javaClass.packageName, path.parent.fileName.toString())
      file.println()
      file.format("import javafx.beans.binding.*%n")
      file.format("import javafx.beans.binding.Bindings.*%n")
      file.format("import javafx.beans.value.*%n")
      file.println()
      code(file)
    }
  }

  private fun mt(type: String) = if (type == "Int") "Integer" else type
  private fun nt(vararg ts: String) = when {
    ts.any { it == "Double" } -> "Double"
    ts.any { it == "Float" } -> "Float"
    ts.any { it == "Long" } -> "Long"
    else -> "Int"
  }
}