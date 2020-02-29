package org.marid.collections;

import java.util.Comparator;
import java.util.List;

public interface MaridCollections {

  static <E> void sort(List<E> list, Comparator<E> comparator) {
    list.sort(comparator);
  }
}
