package org.imdex.tractor.mailbox

/**
  * Created by a.tsukanov on 16.07.2016.
  */
trait BoundedMailboxOverflowStrategy {
    def onLatecomer(envelope: Envelope): Unit
}

object BoundedMailboxOverflowStrategy {
    val Drop: BoundedMailboxOverflowStrategy = _ => {}

    // TODO: dead letters
}