package org.marid.fx

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList
import kotlin.test.assertEquals

@Tag("normal")
class CollectionTest {

  @Test
  fun sort() {
    val changed = AtomicBoolean()
    val list = object : ArrayList<String>(listOf("1", "7", "2", "10", "3")) {
      override fun sort(c: Comparator<in String>?) {
        changed.set(true)
        super.sort(c)
      }
    }
    list.sortWith(compareBy { it })
    assertTrue(changed.get())
  }

  @Test
  fun sortSubList() {
    val permutations = ArrayList<List<Int>>()
    val list = FXCollections.observableArrayList(1, 7, 2, 10, 3, 11);
    list.addListener(ListChangeListener { c ->
      while (c.next()) {
        if (c.wasPermutated()) {
          permutations += (c.from until c.to).map { c.getPermutation(it) }
        }
      }
    })
    list.subList(2, 5).sortWith(compareBy { it })
    assertEquals(0, permutations.size)
  }
}