package org.imdex.tractor.mailbox

import org.imdex.tractor.concurrent.{BoundedMPSCQueue, UnboundedMPSCQueue}

/**
  * Created by a.tsukanov on 16.07.2016.
  */
sealed trait Mailbox {
    protected def messageQueue: MessageQueue[Envelope]

    def isEmpty: Boolean = messageQueue.isEmpty
    def nonEmpty: Boolean = messageQueue.nonEmpty

    def put(envelope: Envelope): Unit
    def pop: Envelope = messageQueue.dequeue

    final def +=(envelope: Envelope): Unit = put(envelope)
}

object UnboundedMailbox {
    def newMpscMailbox(): UnboundedMailbox = new UnboundedMailbox(new UnboundedMessageQueueFactory {
        override def apply[T >: Null]: UnboundedMessageQueue[T] = new UnboundedMPSCQueue[T]
    })
}

final class UnboundedMailbox(createQueue: UnboundedMessageQueueFactory) extends Mailbox {
    override protected val messageQueue = createQueue[Envelope]

    override def put(envelope: Envelope): Unit = messageQueue += envelope
}

object BoundedMailbox {
    def newMpscMailbox(capacity: Int, overflowStrategy: BoundedMailboxOverflowStrategy): BoundedMailbox = new BoundedMailbox(
        new BoundedMessageQueueFactory {
            override def apply[T >: Null](capacity: Int): BoundedMessageQueue[T] = new BoundedMPSCQueue[T](capacity)
        },
        capacity,
        overflowStrategy
    )
}

final class BoundedMailbox(createQueue: BoundedMessageQueueFactory,
                           val capacity: Int,
                           overflowStrategy: BoundedMailboxOverflowStrategy) extends Mailbox {
    override protected val messageQueue = createQueue[Envelope](capacity)

    override def put(envelope: Envelope): Unit = {
        if (!messageQueue.enqueue(envelope))
            overflowStrategy.onLatecomer(envelope)
    }
}
