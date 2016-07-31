package org.imdex.tractor.actor

import org.imdex.tractor.union.{Union, |, ∅}

object Actor {
    type Of[T] = Actor[T | ∅]
}

/**
  * Created by a.tsukanov on 16.07.2016.
  */
/*abstract class*/trait Actor[T <: Union] {
    // TODO: think about hashCode

    val self: Ref.Of[Any] = null

    def receive(implicit context: ReceiveContext): PartialFunction[Any, Any]

    def unhandled(message: Any): Unit = () // TODO: blackhole

    def suspend(): Unit // TODO: suspend actor dispatching

    def resume(): Unit // TODO: resume actor dispatching
}