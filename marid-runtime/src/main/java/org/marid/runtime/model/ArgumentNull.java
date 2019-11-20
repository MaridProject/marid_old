package org.marid.runtime.model;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class ArgumentNull extends Argument {

  public ArgumentNull() {
  }

  public ArgumentNull(@NotNull Element element) {
    super(element);
  }

  public ArgumentNull(@NotNull InputSource source) {
    this(element(source));
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ArgumentNull;
  }

  @Override
  public @NotNull String getTag() {
    return "null";
  }

  @Override
  public void writeTo(@NotNull Element element) {
  }
}
