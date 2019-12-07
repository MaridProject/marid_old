package org.marid.ide

import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.marid.test.Normal
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@Category(Array(classOf[Normal]))
@RunWith(classOf[JUnitRunner])
class FirstSpec extends AnyWordSpec
    with Matchers {

    "x" should {
        "do" in {
            1 shouldBe 1
        }
    }
}
