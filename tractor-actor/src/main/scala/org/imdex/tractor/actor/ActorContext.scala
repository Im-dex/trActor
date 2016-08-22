package org.imdex.tractor.actor

import org.imdex.tractor.dispatch.Dispatcher
import org.imdex.tractor.mailbox.Envelope

/**
  * Created by a.tsukanov on 06.08.2016.
  */
trait ActorContext {
    def environment: Environment
    def dispatcher: Dispatcher

    private[tractor] def enqueue(letter: Envelope): Unit
}
