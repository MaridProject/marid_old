package org.marid.runtime.model;

public interface Entity {

  @Override
  int hashCode();

  @Override
  boolean equals(Object obj);

  @Override
  String toString();
}
