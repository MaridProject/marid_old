package org.marid.runtime.model;

public interface RackInstanceConsumer<E> {

  void accept(E instance) throws Throwable;
}
