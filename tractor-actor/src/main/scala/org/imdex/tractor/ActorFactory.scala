package org.imdex.tractor

import org.imdex.tractor.actor.{Actor, Ref}
import org.imdex.tractor.meta.ActorFactoryMacros
import org.imdex.tractor.union.Union

import scala.language.experimental.macros

private[tractor] sealed trait ActorInstanceCreator[T <: Actor[_]] {
    def create: T
}

/**
  * Created by a.tsukanov on 21.07.2016.
  */
private[tractor] trait ActorFactory {
    private def spawn[Messages <: Union, T <: Actor[_]](builder: ActorInstanceCreator[T]): Ref[Messages] = null

    def spawn[Messages <: Union](the: The[_ <: Actor[Messages]], args: Any*): Ref[Messages] = macro ActorFactoryMacros.spawn[Messages]
}