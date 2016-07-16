package org.imdex.tractor.concurrent

import java.util.concurrent.atomic.AtomicReference

import org.imdex.tractor.mailbox.BoundedMessageQueue

import scala.annotation.tailrec

object BoundedMPSCQueue {
    private object Node {
        def empty[T >: Null]: Node[T] = new Node[T](null, new AtomicReference[Node[T]](null), 0)
    }

    private final class Node[T](var value: T, val next: AtomicReference[Node[T]], var count: Int) {
        def this(value: T) = this(value, new AtomicReference[Node[T]](null), 0)
    }
}

/**
  * Created by a.tsukanov on 16.07.2016.
  */
final class BoundedMPSCQueue[T >: Null](val capacity: Int) extends BoundedMessageQueue[T] {
    import BoundedMPSCQueue._

    @sun.misc.Contended // TODO: bench
    private[this] val (_head, _tail) = {
        val empty = Node.empty[T]
        new AtomicReference(empty) -> new AtomicReference(empty)
    }

    override def isEmpty: Boolean = _head.get eq _tail.get

    @tailrec
    private def enqueue(node: Node[T], value: T): Boolean = {
        val last = _tail.get // tail acquire
        val lastCount = last.count

        if (lastCount - _head.get.count < capacity) { // head acquire
            val enqueueNode = if (node == null) new Node(value) else node // delayed node creation to avoid node allocation when the queue is full
            enqueueNode.count = lastCount + 1

            if (_tail.compareAndSet(last, enqueueNode)) { // tail acquire - release
                last.next.set(enqueueNode)
                true
            } else {
                enqueue(enqueueNode, value) // backoff
            }
        } else {
            false
        }
    }

    override def enqueue(value: T): Boolean = enqueue(null, value)

    @tailrec
    override def dequeue: T = {
        val head = _head.get // head acquire
        val next = head.next.get // next acquire

        if (next != null) {
            if (_head.compareAndSet(head, next)) { // head acquire - release
                val value = next.value
                next.value = null
                value
            } else {
                dequeue // backoff
            }
        } else if (_tail.get eq head) {
            null // empty
        } else {
            dequeue // backoff
        }
    }

    override def peek: T = {
        val head = _head.get
        val next = head.next.get

        if ((next != null) || (_tail.get eq head)) {
            if (next == null) null else next.value
        } else {
            peek
        }
    }

    override def clear(): Unit = {
        val empty = Node.empty[T]
        _tail.set(empty)
        _head.set(empty)
    }
}
