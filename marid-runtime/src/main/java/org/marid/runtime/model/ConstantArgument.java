package org.marid.runtime.model;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.function.Function;
import java.util.stream.Stream;

public abstract class ConstantArgument extends Argument {

  ConstantArgument() {
  }

  ConstantArgument(@NotNull Element element) {
    super(element);
  }

  private static Stream<Function<Element, ConstantArgument>> constructors() {
    return Stream.of(
        ArgumentConstRef::new,
        ArgumentLiteral::new
    );
  }

  static ConstantArgument argument(Element element) {
    return constructors()
        .flatMap(c -> {
          try {
            return Stream.of(c.apply(element));
          } catch (IllegalArgumentException e) {
            return Stream.empty();
          }
        })
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown argument: " + element.getTagName()));
  }

  static Argument argument(InputSource inputSource) {
    return argument(AbstractEntity.element(inputSource));
  }
}
