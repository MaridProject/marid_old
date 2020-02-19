package org.marid.runtime.model;

import java.util.List;

public interface CellarConstant extends Entity, HasVarargs {

  String getFactory();

  void setFactory(String factory);

  String getSelector();

  void setSelector(String selector);

  String getName();

  void setName(String name);

  List<? extends ConstantArgument> getArguments();

  void addArgument(ConstantArgument argument);
}
