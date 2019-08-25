package org.marid.project.model;

import org.jetbrains.annotations.NotNull;
import org.marid.xml.XmlStreams;
import org.marid.xml.XmlUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RackInput extends AbstractEntity {

  private String cellar;
  private String rack;
  private String method;
  private final ArrayList<Argument> arguments;

  public RackInput(@NotNull String cellar,
                   @NotNull String rack,
                   @NotNull String method,
                   @NotNull List<@NotNull Argument> arguments) {
    this.cellar = cellar;
    this.rack = rack;
    this.method = method;
    this.arguments = new ArrayList<>(arguments);
  }

  public RackInput(@NotNull Element element) {
    this.cellar = element.getAttribute("cellar");
    this.rack = element.getAttribute("rack");
    this.method = element.getAttribute("method");
    this.arguments = XmlStreams.children(element, Element.class)
        .map(ArgumentFactory::argument)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public RackInput(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  @Override
  public @NotNull String getTag() {
    return "input";
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("cellar", cellar);
    element.setAttribute("rack", rack);
    element.setAttribute("method", method);
    arguments.forEach(a -> XmlUtils.appendTo(a, element));
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellar, rack, method, arguments);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RackInput) {
      final var that = (RackInput) obj;
      return Objects.equals(this.cellar, that.cellar)
          && Objects.equals(this.rack, that.rack)
          && Objects.equals(this.method, that.method)
          && Objects.equals(this.arguments, that.arguments);
    }
    return false;
  }
}
