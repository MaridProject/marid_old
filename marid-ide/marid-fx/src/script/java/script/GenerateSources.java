package script;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GenerateSources {

  private static final Set<String> numerics = new LinkedHashSet<>(Arrays.asList("Double", "Float", "Long", "Int"));
  private static final Set<String> types = concat(numerics, "String", "Boolean");
  private static final Set<String> cmps = concat(numerics, "String");
  private static final Set<String> nullables = Collections.singleton("String");

  private static final Map<String, String> numericOps = Map.of(
      "minus", "subtract",
      "plus", "add",
      "times", "multiply",
      "div", "divide"
  );

  public static void main(String... args) throws Exception {
    final var genSourcesDir = Path.of(args[0]);

    gen(genSourcesDir, "BindingExtensions", file -> {
      file.format("fun <T, R> ObservableValue<T>.map(func: (T) -> R): ObjectBinding<R> = createObjectBinding(Callable { func(value)}, this)%n");
      types.forEach(f -> {
        file.format("fun <T> ObservableValue<T>.map%1$s(func: (T) -> %2$s) = create%1$sBinding(Callable { func(value) }, this)%n", f, t(f));
        file.format("fun <R> Observable%2$sValue.mapObject(func: (%1$s) -> R): ObjectBinding<R> = createObjectBinding(Callable {func(get()) }, this)%n", t(f), f);
        types.forEach(t -> {
          if (t.equals(f)) {
            file.format("fun Observable%1$sValue.map(func: (%1$s) -> %1$s) = create%1$sBinding(Callable { func(get()) }, this)%n", f);
          } else {
            file.format("fun Observable%1$sValue.map%2$s(func: (%3$s) -> %4$s) = create%2$sBinding(Callable { func(get()) }, this)%n", f, t, t(f), t(t));
          }
        });
      });
    });

    gen(genSourcesDir, "BindingNumericOpsExtensions", file -> numericOps.forEach((op, method) -> {
      file.format("// %s --> %s%n", op, method);
      for (final var p : numerics) {
        for (final var pn : numerics) {
          file.format("fun Observable%sValue.%s(v: %s) = %s(this, v) as %sBinding%n", p, op, pn, method, c(p, pn));
          file.format("fun %s.%s(v: Observable%sValue) = %s(this, v) as %sBinding%n", p, op, pn, method, c(p, pn));
          file.format("fun Observable%sValue.%s(v: Observable%sValue) = %s(this, v) as %sBinding%n", p, op, pn, method, c(p, pn));
        }
      }
      file.println();
    }));
  }

  private static void gen(Path base, String name, Consumer<PrintWriter> code) throws IOException {
    final var path = base.resolve("org").resolve("marid").resolve("fx").resolve("extensions");
    Files.createDirectories(path);
    try (final var w = new PrintWriter(Files.newBufferedWriter(path.resolve(name + ".kt"), UTF_8))) {
      w.format("package org.marid.fx.extensions%n");
      w.println();
      w.format("import javafx.beans.binding.*%n");
      w.format("import javafx.beans.binding.Bindings.*%n");
      w.format("import javafx.beans.value.*%n");
      w.format("import java.util.concurrent.Callable%n");
      w.format("import org.marid.fx.extensions.ExtensionHelpers.createIntBinding%n");
      w.println();
      code.accept(w);
    }
  }

  private static String c(String... types) {
    if (Arrays.asList(types).contains("Double")) {
      return "Double";
    }
    if (Arrays.asList(types).contains("Float")) {
      return "Float";
    }
    if (Arrays.asList(types).contains("Long")) {
      return "Long";
    }
    return "Int";
  }

  private static String t(String type) {
    return nullables.contains(type) ? type + "?" : type;
  }

  private static LinkedHashSet<String> concat(Set<String> set, String... elements) {
    final var result = new LinkedHashSet<String>(set.size() + elements.length);
    result.addAll(set);
    Collections.addAll(result, elements);
    return result;
  }
}
