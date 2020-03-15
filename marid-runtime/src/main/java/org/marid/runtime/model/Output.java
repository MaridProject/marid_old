package org.marid.runtime.model;

public interface Output extends Entity {

  String getName();

  void setName(String name);

  @Override default String tag() {return "output";}
}
