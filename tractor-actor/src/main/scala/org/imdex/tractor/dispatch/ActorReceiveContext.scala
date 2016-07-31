package org.imdex.tractor.dispatch

import org.imdex.tractor.actor.{ReceiveContext, Ref}
import org.imdex.tractor.union.∅

/**
  * Created by a.tsukanov on 29.07.2016.
  */
private[dispatch] final class ActorReceiveContext(var sender: Ref[∅]) extends ReceiveContext
