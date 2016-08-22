package org.imdex.tractor.actor

import java.util.concurrent.{Executors, ScheduledExecutorService}

import org.imdex.tractor.dispatch.Dispatcher
import org.imdex.tractor.internal._
import org.imdex.tractor.mailbox.{Mailbox, UnboundedMailbox}
import org.imdex.tractor.union.Union

/**
  * Created by a.tsukanov on 31.07.2016.
  */
final class Environment(val defaultDispatcher: Dispatcher,
                        val defaultMailboxFactory: () => Mailbox) extends ActorFactory {
    private[this] val deadMessagesData = ActorData(this, defaultDispatcher, new DeadMessages, UnboundedMailbox.newMpscMailbox(),
                                                   new InternalReceiveContext(null), ExecutionState.Idle)

    private[this] val root = ActorData(this, defaultDispatcher, new EnvironmentRoot, null, null, ExecutionState.Idle)

    defaultDispatcher.register(deadMessagesData)

    private[tractor] val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private[tractor] val deadMessagesMailbox: Mailbox = UnboundedMailbox.newMpscMailbox()

    override private[tractor] def spawn[Messages <: Union, T <: JustActor](builder: ActorInstanceCreator[T]): Ref[Messages] = {
        root.actor.spawn[Messages, T](builder)
    }

    val deadMessages: Ref.Of[Any] = LocalRef(deadMessagesData)
}
