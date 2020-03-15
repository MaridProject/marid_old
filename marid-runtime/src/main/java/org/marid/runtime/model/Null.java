package org.marid.runtime.model;

public interface Null extends ConstantArgument {
  @Override default String tag() {return "null";}
}
