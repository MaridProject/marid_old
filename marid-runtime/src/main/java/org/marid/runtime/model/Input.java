package org.marid.runtime.model;

import org.jetbrains.annotations.NotNull;
import org.marid.xml.XmlStreams;
import org.marid.xml.XmlUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.Objects;

public final class Input extends AbstractEntity {

  public String name;
  public Argument argument;

  public Input(@NotNull String name, @NotNull Argument argument) {
    this.name = name;
    this.argument = argument;
  }

  public Input(@NotNull Element element) {
    super(element);
    this.name = element.getAttribute("name");
    this.argument = XmlStreams.elementsByTag(element, "arg")
        .findFirst()
        .map(ArgumentFactory::argument)
        .orElseThrow();
  }

  public Input(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  @Override
  public @NotNull String getTag() {
    return "in";
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("name", name);
    XmlUtils.append(element, "arg", argument::writeTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, argument);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Input) {
      final var that = (Input) obj;
      return Objects.equals(this.name, that.name) && Objects.equals(this.argument, that.argument);
    }
    return false;
  }
}
