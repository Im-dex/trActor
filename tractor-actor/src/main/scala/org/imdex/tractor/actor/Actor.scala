package org.imdex.tractor.actor

import org.imdex.tractor.internal._
import org.imdex.tractor.union.{Union, |, ∅}

import scala.collection.mutable

object Actor {
    type Of[T] = Actor[T | ∅]

    private[tractor] val context = new ThreadLocal[ActorContext]()
}

/**
  * Created by a.tsukanov on 16.07.2016.
  */
trait Actor[T <: Union] extends ActorFactory {
    private[this] val children = mutable.HashMap.empty[ActorIndex, ActorData]

    final override private[tractor] def spawn[Messages <: Union, U <: JustActor](builder: ActorInstanceCreator[U]): Ref[Messages] = {
        val conf = builder.conf
        val environment = context.environment

        val defaultDispatcher = context.environment.defaultDispatcher
        val defaultMailboxFactory = environment.defaultMailboxFactory

        val dispatcher = if (conf.dispatcher eq null) defaultDispatcher else conf.dispatcher
        val mailbox = if (conf.mailbox eq null) defaultMailboxFactory() else conf.mailbox

        val instance = builder.create
        val receiveContext = new InternalReceiveContext(null)
        val data = ActorData(environment, dispatcher, instance, mailbox, receiveContext, ExecutionState.Work)

        dispatcher.register(data)
        LocalRef(data)
    }

    final val context = Actor.context.get()
    final val self: Ref.Of[Any] = LocalRef(context)

    def receive(implicit context: ReceiveContext): PartialFunction[Any, Any]

    def unhandled(message: Any): Unit = context.environment.deadMessages ! message

    def suspend(): Unit = ??? // TODO: suspend actor dispatching

    def resume(): Unit = ??? // TODO: resume actor dispatching
}