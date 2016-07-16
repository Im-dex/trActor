package org.imdex.tractor.mailbox

/**
  * Created by a.tsukanov on 16.07.2016.
  */
sealed trait MessageQueueFactory

trait UnboundedMessageQueueFactory extends MessageQueueFactory {
    def apply[T >: Null]: UnboundedMessageQueue[T]
}

trait BoundedMessageQueueFactory extends MessageQueueFactory {
    def apply[T >: Null](capacity: Int): BoundedMessageQueue[T]
}