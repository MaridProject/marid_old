package org.marid.project.model;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

public class RackConstant extends AbstractEntity {

  private String library;

  RackConstant(@NotNull String id, @NotNull String name, @NotNull String library) {
    super(id, name);
    this.library = library;
  }

  RackConstant(Element element) {
    super(element);
  }

  @Override
  String tag() {
    return "constant";
  }

  public String getLibrary() {
    return library;
  }

  public void setLibrary(String library) {
    this.library = library;
  }
}
