package org.imdex.tractor

import org.imdex.tractor.actor.{ReceiveContext, Ref}
import org.imdex.tractor.union.Union

/**
  * Created by a.tsukanov on 21.07.2016.
  */
trait Sender[T <: Union] extends Any with Serializable {
    def sender(implicit context: ReceiveContext): Ref[T] = context.sender.copyAs[T]
}