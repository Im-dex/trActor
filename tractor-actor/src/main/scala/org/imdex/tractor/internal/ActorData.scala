package org.imdex.tractor.internal

import java.util.concurrent.atomic.AtomicReference

import org.imdex.tractor.actor._
import org.imdex.tractor.dispatch.Dispatcher
import org.imdex.tractor.mailbox.{Envelope, Mailbox}

private[tractor] sealed trait ExecutionState
private[tractor] object ExecutionState {
    case object Idle extends ExecutionState // Mailbox is empty, actor suspended until next received message
    case object Work extends ExecutionState // Mailbox processing in progress
    case object Suspended extends ExecutionState // Mailbox processing was suspended by the actor
    case object Stopped extends ExecutionState // Actor is stopped
}

private[tractor] object ActorData {
    def apply(env: Environment, dispatcher: Dispatcher, actor: JustActor, mailbox: Mailbox, receiveContext: InternalReceiveContext, state: ExecutionState): ActorData = {
        new ActorData(env, dispatcher, actor, mailbox, new AtomicReference(state), actor.receive(receiveContext), receiveContext)
    }
}

/**
  * Created by a.tsukanov on 29.07.2016.
  */
private[tractor] final class ActorData(override val environment: Environment,
                                       override val dispatcher: Dispatcher,
                                       var actor: JustActor, // NOTE: var only to avoid dangling actor references when actor is dead
                                       var mailbox: Mailbox,
                                       val state: AtomicReference[ExecutionState],
                                       var receive: PartialFunction[Any, Any], // NOTE: var only to avoid dangling actor references when actor is dead
                                       val receiveContext: InternalReceiveContext) extends ActorContext {
    override def equals(obj: scala.Any): Boolean = obj match {
        case that: ActorData => actor eq that.actor
        case _               => false
    }

    override def hashCode(): Int = actor.hashCode()

    override def enqueue(letter: Envelope): Unit = {
        import ExecutionState._

        val idle = state.compareAndSet(Idle, Work) // synchronization point
        mailbox += letter
        if (idle) dispatcher.notifyResume(this)
    }
}