package org.imdex.tractor.dispatch

import java.util.concurrent.{ExecutorService, RejectedExecutionException}
import java.util.concurrent.atomic.AtomicInteger

import org.imdex.tractor.actor.Ref
import org.imdex.tractor.internal.{ActorData, ActorIndex, ExecutionState}
import org.imdex.tractor.mailbox.Envelope

import scala.annotation.tailrec
import scala.concurrent.ExecutionContextExecutor

object Dispatcher {
    val DefaultThroughput = 1
}

/**
  * Created by a.tsukanov on 26.07.2016.
  */
abstract class Dispatcher(executorService: ExecutorService,
                          throughput: Int = Dispatcher.DefaultThroughput) extends ExecutionContextExecutor {
    private[this] val index: AtomicInteger = new AtomicInteger(0)

    private def newIndex: ActorIndex = ActorIndex(index.getAndIncrement)

    private def processMessage(data: ActorData, envelope: Envelope): Unit = {
        import data._
        receiveContext.sender = if (envelope.sender eq Ref.NoSender) data.environment.deadMessages else envelope.sender
        receive.applyOrElse(envelope.message, actor.unhandled)
    }

    @tailrec
    private def processMessages(data: ActorData, count: Int): Unit = {
        val envelope = data.mailbox.pop

        if ((count < throughput) && (envelope ne null)) {
            processMessage(data, envelope)
            processMessages(data, count + 1)
        }
    }

    private[tractor] def notifyResume(data: ActorData): Unit = try {
        execute(beginProcessMessages(data))
    } catch {
        case _: RejectedExecutionException => data.environment.scheduler
    }

    private[tractor] def register(data: ActorData): ActorIndex = {
        val index = newIndex
        notifyResume(data) // TODO: throw if err
        index
    }

    private[tractor] def unregister(data: ActorData): Unit = {
        data.mailbox = data.environment.deadMessagesMailbox
        data.state.set(ExecutionState.Stopped) // synchronization point
    }

    protected def beginProcessMessages(data: ActorData): Unit = {
        val envelope = data.mailbox.pop

        if (envelope eq null) {
            data.state.compareAndSet(ExecutionState.Work, ExecutionState.Idle)
        } else {
            processMessage(data, envelope)
            processMessages(data, 1)
        }
    }

    final def execute(function: => Any): Unit = execute((() => function): Runnable) // TODO: exceptions

    override def reportFailure(cause: Throwable): Unit = ??? // TODO: logging

    override def execute(command: Runnable): Unit = executorService.execute(command) // TODO: exceptions
}
