package org.marid.runtime;

@FunctionalInterface
public interface EntryPoint {

  void run(Context context);
}
