package org.marid.runtime.model;

import java.util.List;

public interface Rack extends Entity, HasVarargs {

  String getName();

  void setName(String name);

  String getFactory();

  void setFactory(String factory);

  List<? extends Argument> getArguments();

  List<? extends Initializer> getInitializers();

  void addArgument(Argument argument);

  void addInitializer(Initializer initializer);
}
