package org.imdex.tractor.concurrent

import java.util.concurrent.atomic.AtomicReference

import org.imdex.tractor.mailbox.UnboundedMessageQueue

import scala.annotation.tailrec

object UnboundedMPSCQueue {
    private object Node {
        def empty[T >: Null]: Node[T] = new Node[T](null, new AtomicReference[Node[T]](null))
    }

    private final class Node[T](var value: T, val next: AtomicReference[Node[T]]) {
        def this(value: T) = this(value, new AtomicReference[Node[T]](null))
    }
}

/**
  * Created by a.tsukanov on 16.07.2016.
  */
final class UnboundedMPSCQueue[T >: Null] extends UnboundedMessageQueue[T] {
    import UnboundedMPSCQueue._

    @sun.misc.Contended // TODO: bench
    private[this] val (_head, _tail) = {
        val empty = Node.empty[T]
        new AtomicReference(empty) -> new AtomicReference(empty)
    }

    @tailrec
    private def wait(tail: Node[T]): Node[T] = {
        val next = tail.next.get
        if (next == null) wait(tail) else next
    }

    override def isEmpty: Boolean = _head.get eq _tail.get

    override def enqueue(value: T): Unit = {
        val node = new Node(value)
        _head.getAndSet(node).next.set(node)
    }

    override def dequeue: T = {
        val tail = _tail.get
        val next = tail.next.get

        val next2 =
            if ((next == null) && (_head.get ne tail)) wait(tail)
            else                                       next

        if (next2 != null) {
            val value = next.value
            next.value = null
            _tail.set(next)
            tail.next.set(null)
            value
        } else {
            null
        }
    }

    override def peek: T = {
        val tail = _tail.get
        val next = tail.next.get

        val node =
            if ((next == null) && (_head.get ne tail)) wait(tail)
            else                                       next

        if (node == null) null else node.value
    }

    override def clear(): Unit = {
        val empty = Node.empty[T]
        _tail.set(empty)
        _head.set(empty)
    }
}
