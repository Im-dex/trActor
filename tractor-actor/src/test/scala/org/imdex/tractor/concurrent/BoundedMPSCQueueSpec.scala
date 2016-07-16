package org.imdex.tractor.concurrent

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by a.tsukanov on 16.07.2016.
  */
class BoundedMPSCQueueSpec extends FlatSpec with Matchers {
    "BoundedMPSCQueue" should "preserve elements order in case of race free execution" in {
        val queue = new BoundedMPSCQueue[Integer](10)

        (queue += 0) shouldBe true
        (queue += 1) shouldBe true
        (queue += 2) shouldBe true

        queue.dequeue shouldBe 0
        queue.dequeue shouldBe 1
        queue.dequeue shouldBe 2
    }

    it should "stop inserting elements when full" in  {
        val queue = new BoundedMPSCQueue[Integer](3)

        (queue += 0) shouldBe true
        (queue += 0) shouldBe true
        (queue += 0) shouldBe true

        (queue += 0) shouldBe false
        (queue += 0) shouldBe false

        queue.dequeue

        (queue += 0) shouldBe true
        (queue += 0) shouldBe false
    }
}
