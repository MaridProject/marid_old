package org.marid.runtime.model;

import java.util.List;

public interface Rack extends Entity {

  String getName();

  void setName(String name);

  String getFactory();

  void setFactory(String factory);

  List<? extends Argument> getArguments();

  List<? extends Input> getInputs();

  List<? extends Initializer> getInitializers();

  void addArgument(Argument argument);

  void addInput(Input input);

  void addInitializer(Initializer initializer);
}
