package org.imdex.tractor.dispatch

import java.util.concurrent.atomic.AtomicReference

import org.imdex.tractor.actor.{JustActor, Environment}
import org.imdex.tractor.mailbox.{Envelope, Mailbox}

private[dispatch] sealed trait ActorState
private[dispatch] object ActorState {
    case object Idle extends ActorState // Mailbox is empty, actor suspended until next received message
    case object Work extends ActorState // Mailbox processing in progress
    case object Suspended extends ActorState // Mailbox processing was suspended by the actor
    case object Stopped extends ActorState // Actor is stopped
}

private[tractor] sealed trait ActorContext {
    def environment: Environment
    def dispatcher: Dispatcher
    def enqueue(letter: Envelope): Unit
}

private[dispatch] object ActorData {
    def apply(env: Environment, dispatcher: Dispatcher, actor: JustActor, mailbox: Mailbox, receiveContext: ActorReceiveContext, state: ActorState): ActorData = {
        new ActorData(env, dispatcher, actor, mailbox, new AtomicReference(state), actor.receive(receiveContext), receiveContext)
    }
}

/**
  * Created by a.tsukanov on 29.07.2016.
  */
private[dispatch] final class ActorData(override val environment: Environment,
                                        override val dispatcher: Dispatcher,
                                        var actor: JustActor, // NOTE: var only to avoid dangling actor references when actor is dead
                                        var mailbox: Mailbox,
                                        val state: AtomicReference[ActorState],
                                        var receive: PartialFunction[Any, Any], // NOTE: var only to avoid dangling actor references when actor is dead
                                        val receiveContext: ActorReceiveContext) extends ActorContext {
    override def equals(obj: scala.Any): Boolean = obj match {
        case that: ActorData => actor eq that.actor
        case _               => false
    }

    override def hashCode(): Int = actor.hashCode()

    override def enqueue(letter: Envelope): Unit = {
        import ActorState._

        val idle = state.compareAndSet(Idle, Work) // synchronization point
        mailbox += letter
        if (idle) dispatcher.notifyResume(this)
    }
}