package org.imdex.tractor.mailbox

/**
  * Created by a.tsukanov on 16.07.2016.
  */
sealed trait Mailbox {
    protected def messageQueue: MessageQueue[Envelope]

    def isEmpty: Boolean = messageQueue.isEmpty
    def nonEmpty: Boolean = messageQueue.nonEmpty

    def put(envelope: Envelope): Unit
    def pop: Envelope = messageQueue.dequeue
}

final class UnboundedMailbox(createQueue: UnboundedMessageQueueFactory) extends Mailbox {
    override protected val messageQueue = createQueue[Envelope]

    override def put(envelope: Envelope): Unit = messageQueue += envelope
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
