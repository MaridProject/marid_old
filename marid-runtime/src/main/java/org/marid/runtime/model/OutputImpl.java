package org.marid.runtime.model;

public class OutputImpl extends AbstractEntity implements Output {

  private String name;

  OutputImpl() {}

  public OutputImpl(String name) {
    this.name = name;
  }

  @Override public String getName() { return name; }
  @Override public void setName(String name) { this.name = name; }
}
