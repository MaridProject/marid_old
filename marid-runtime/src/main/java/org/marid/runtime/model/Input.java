package org.marid.runtime.model;

public interface Input extends Entity {

  String getName();

  void setName(String name);

  Argument getArgument();

  void setArgument(Argument argument);
}
