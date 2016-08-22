package org.imdex.tractor.internal

import org.imdex.tractor.actor.{Actor, ReceiveContext}
import org.imdex.tractor.union.∅

/**
  * Created by a.tsukanov on 06.08.2016.
  */
private[tractor] class EnvironmentRoot extends Actor[∅] {
    override def receive(implicit context: ReceiveContext) = {
        case _ =>
    }
}
