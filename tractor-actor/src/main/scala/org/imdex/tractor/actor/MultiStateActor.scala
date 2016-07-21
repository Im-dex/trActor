package org.imdex.tractor.actor

import org.imdex.tractor.union.{Union, |, ∅}

trait ActorState {
    def onEnter(): Unit = ()
    def onLeave(): Unit = ()

    def receive(implicit context: ReceiveContext): PartialFunction[Any, Any]
}

object MultiStateActor {
    type Of[T] = MultiStateActor[T | ∅]
}

/**
  * Created by a.tsukanov on 16.07.2016.
  */
trait MultiStateActor[T <: Union] extends Actor[T] {
    private[this] var state = initialState
    state.onEnter()

    final def become(nextState: ActorState): Unit = {
        state.onLeave()
        state = nextState
        state.onEnter()
    }

    final override def receive(implicit context: ReceiveContext): PartialFunction[Any, Any] = {
        case message => state.receive(context).applyOrElse(message, unhandled)
    }

    def initialState: ActorState
}
