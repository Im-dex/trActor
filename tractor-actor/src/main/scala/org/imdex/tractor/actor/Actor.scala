package org.imdex.tractor.actor

import org.imdex.tractor.union.{Union, |, ∅}

object Actor {
    type Of[T] = Actor[T | ∅]
}

/**
  * Created by a.tsukanov on 16.07.2016.
  */
/*abstract class*/trait Actor[T <: Union] {
    val self: Any = null

    def receive(implicit context: ReceiveContext): PartialFunction[Any, Any]

    def unhandled(message: Any): Unit = () // TODO: dead letters
}