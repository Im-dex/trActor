package org.imdex.tractor.util

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by a.tsukanov on 26.07.2016.
  */
class TraversableLikeExtensionSpec extends FlatSpec with Matchers {
    "Extension method split" should "split container into `lengths.size` parts of corresponding length" in {
        (1 :: 2 :: 3 :: 4 :: 5 :: 6 :: Nil).split(2, 3) shouldBe List(
            1 :: 2 :: Nil,
            3 :: 4 :: 5 :: Nil,
            6 :: Nil
        )
    }

    "Extension method split" should "skip empty trailing part" in {
        (1 :: 2 :: 3 :: 4 :: 5 :: Nil).split(2, 3) shouldBe List(
            1 :: 2 :: Nil,
            3 :: 4 :: 5 :: Nil
        )
    }
}
