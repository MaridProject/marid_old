import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GenerateSources {

  private static final List<String> numericTypes = List.of("Double", "Float", "Long", "Int");

  private static final List<String> types = Stream.concat(
      numericTypes.stream(),
      Stream.of("String", "Boolean")
  ).collect(Collectors.toUnmodifiableList());

  private static final List<String> cmpTypes = Stream.concat(
      numericTypes.stream(),
      Stream.of("String")
  ).collect(Collectors.toUnmodifiableList());

  private static final Map<String, String> numericOps = List.of(
      Map.entry("minus", "subtract"),
      Map.entry("plus", "add"),
      Map.entry("times", "multiply"),
      Map.entry("div", "divide")
  ).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new));

  public static void main(String... args) throws Exception {
    final var genSourcesDir = Path.of(args[0]);

    gen(genSourcesDir, "BindingExtensions", file -> {
      file.format("fun <T, R> ObservableValue<T>.map(func: (T) -> R): ObjectBinding<R> = createObjectBinding(java.util.concurrent.Callable { func(value)}, this)%n");
      types.forEach(p -> {
        final var t = mt(p);
        file.format("fun <T> ObservableValue<T>.map%s(func: (T) -> %s): %sBinding = create%sBinding(java.util.concurrent.Callable { func(value) }, this)%n", p, p, t, t);
        types.forEach(pn -> {
          if (pn.equals(p)) {
            file.format("fun Observable%sValue.map(func: (%s) -> %s): %sBinding = create%sBinding(java.util.concurrent.Callable { func(get()) }, this)%n", t, p, p, t, t);
          } else {
            final var tn = mt(pn);
            file.format("fun Observable%sValue.map%s(func: (%s) -> %s): %sBinding = create%sBinding(java.util.concurrent.Callable { func(get()) }, this)%n", t, pn, p, pn, tn, tn);
          }
        });
      });
    });

    gen(genSourcesDir, "BindingNumericOpsExtensions", file -> numericOps.forEach((op, method) -> {
      file.format("// %s --> %s%n", op, method);
      for (final var p : numericTypes) {
        for (final var pn : numericTypes) {
          final var rt = mt(nt(p, pn));
          final var t = mt(p);
          final var tn = mt(pn);
          file.format("fun Observable%sValue.%s(v: %s): %sBinding = %s(this, v) as %sBinding%n", t, op, pn, rt, method, rt);
          file.format("fun %s.%s(v: Observable%sValue): %sBinding = %s(this, v) as %sBinding%n", p, op, tn, rt, method, rt);
          file.format("fun Observable%sValue.%s(v: Observable%sValue): %sBinding = %s(this, v) as %sBinding%n", t, op, tn, rt, method, rt);
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
      w.println();
      code.accept(w);
    }
  }

  private static String mt(String type) {
    return type.equals("Int") ? "Integer" : type;
  }

  private static String nt(String... types) {
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
}
