package org.imdex.tractor.mailbox

/**
  * Created by a.tsukanov on 15.07.2016.
  */
sealed trait MessageQueue[T] {
    def isEmpty: Boolean
    def nonEmpty: Boolean = !isEmpty

    // single consumer warranty
    def dequeue: T

    // single consumer warranty
    def peek: T

    // single consumer warranty
    def clear(): Unit

    // TODO: move all elements
}

trait BoundedMessageQueue[T] extends MessageQueue[T] {
    def enqueue(value: T): Boolean
    def +=(value: T): Boolean = enqueue(value)
}

trait UnboundedMessageQueue[T] extends MessageQueue[T] {
    def enqueue(value: T): Unit
    def +=(value: T): Unit = enqueue(value)
}
