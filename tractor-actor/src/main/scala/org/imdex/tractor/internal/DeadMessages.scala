package org.imdex.tractor.internal

import org.imdex.tractor.actor.{Actor, ReceiveContext}

/**
  * Created by a.tsukanov on 06.08.2016.
  */
private[tractor] class DeadMessages extends Actor.Of[Any] {
    override def receive(implicit context: ReceiveContext) = {
        case _ => // TODO:
    }
}
