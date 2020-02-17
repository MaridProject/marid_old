package org.marid.fx;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@Tag("normal")
class ObservableListTest {

  @Test
  void permutation() {
    final var list = FXCollections.observableArrayList(1, 3, 8, 7, 2, 9);
    final var permutations = new AtomicReference<>(new int[0]);
    list.addListener((ListChangeListener<Integer>) c -> {
      while (c.next()) {
        if (c.wasPermutated()) {
          permutations.set(IntStream.range(c.getFrom(), c.getTo()).map(c::getPermutation).toArray());
        }
      }
    });
    list.sort(Integer::compareTo);
    assertArrayEquals(new int[]{0, 2, 4, 3, 1, 5}, permutations.get());
  }
}
