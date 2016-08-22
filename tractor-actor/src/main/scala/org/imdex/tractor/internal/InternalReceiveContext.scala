package org.imdex.tractor.internal

import org.imdex.tractor.actor.{ReceiveContext, Ref}
import org.imdex.tractor.union.∅

/**
  * Created by a.tsukanov on 29.07.2016.
  */
private[tractor] final class InternalReceiveContext(var sender: Ref[∅]) extends ReceiveContext
