package org.marid.collections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public interface MaridCollectors {

  static <T, L extends List<ArrayList<T>>> Collector<T, L, L> partition(int size, Supplier<L> listSupplier) {
    return new Collector<>() {
      @Override
      public Supplier<L> supplier() {
        return listSupplier;
      }

      @Override
      public BiConsumer<L, T> accumulator() {
        return (a, e) -> {
          if (!a.isEmpty()) {
            final var last = a.get(a.size() - 1);
            if (last.size() < size) {
              last.add(e);
            }
          }
          final var list = new ArrayList<T>(size);
          list.add(e);
          a.add(list);
        };
      }

      @Override
      public BinaryOperator<L> combiner() {
        return (l1, l2) -> l1;
      }

      @Override
      public Function<L, L> finisher() {
        return Function.identity();
      }

      @Override
      public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.IDENTITY_FINISH);
      }
    };
  }

  static <T> Collector<T, LinkedList<ArrayList<T>>, LinkedList<ArrayList<T>>> partition(int size) {
    return partition(size, LinkedList::new);
  }
}
