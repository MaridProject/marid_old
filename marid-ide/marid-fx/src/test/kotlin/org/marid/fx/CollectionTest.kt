package org.marid.fx

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class CollectionTest {

  @Test
  @Tag("broken")
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
  @Tag("normal")
  fun sortSubList() {

  }
}