package org.marid.runtime.model;

import java.util.List;

public interface Initializer extends Entity {

  String getName();

  void setName(String name);

  List<? extends Argument> getArguments();

  void addArgument(Argument argument);
}
