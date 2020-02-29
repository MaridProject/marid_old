package org.marid.fx

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.*

@Tag("broken")
class CollectionTest {

  @Test
  fun sort() {
    var changed = false
    val list = object : ArrayList<String>() {
      override fun sort(c: Comparator<in String>?) {
        changed = true
        super.sort(c)
      }
    }
    list.sortWith(compareBy { it })
    assertTrue(changed)
  }
}