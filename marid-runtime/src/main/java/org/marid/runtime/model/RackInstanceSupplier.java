package org.marid.runtime.model;

@FunctionalInterface
public interface RackInstanceSupplier<E> {

  E get() throws Throwable;
}
