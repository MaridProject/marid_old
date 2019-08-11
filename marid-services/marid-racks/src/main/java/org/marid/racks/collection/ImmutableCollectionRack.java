package org.marid.racks.collection;

import org.marid.runtime.annotation.Output;

import java.util.Collection;

public interface ImmutableCollectionRack<E, C extends Collection<E>> {

  C get();

  @Output(title = "size")
  default int size() {
    return get().size();
  }
}
