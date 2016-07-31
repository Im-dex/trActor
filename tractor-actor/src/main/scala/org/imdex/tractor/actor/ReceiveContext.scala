package org.imdex.tractor.actor

import org.imdex.tractor.union.∅

/**
  * Created by a.tsukanov on 21.07.2016.
  */
trait ReceiveContext {
    def sender: Ref[∅]
}
