package org.imdex.tractor.dispatch

import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicInteger

import org.imdex.tractor.actor.ActorIndex
import org.imdex.tractor.mailbox.Envelope
import org.imdex.tractor.util

import scala.annotation.tailrec
import scala.concurrent.ExecutionContextExecutor

object Dispatcher {
    val DefaultThroughput = 1
}

/**
  * Created by a.tsukanov on 26.07.2016.
  */
abstract class Dispatcher(executorService: ExecutorService, throughput: Int = Dispatcher.DefaultThroughput) extends ExecutionContextExecutor {
    import util._

    private[this] val index: AtomicInteger = new AtomicInteger(0)

    private def newIndex: ActorIndex = ActorIndex(index.getAndIncrement)

    private def processMessage(data: ActorData, envelope: Envelope): Unit = {
        import data._
        receiveContext.sender = envelope.sender
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

    private[dispatch] def notifyResume(data: ActorData): Unit = execute(beginProcessMessages(data))

    private[dispatch] def register(data: ActorData): ActorIndex = {
        val index = newIndex
        register(index, data)
        notifyResume(data)
        index
    }

    protected def beginProcessMessages(data: ActorData): Unit = {
        val envelope = data.mailbox.pop

        if (envelope eq null) {
            data.state := ActorState.Idle
        } else {
            processMessage(data, envelope)
            processMessages(data, 1)
        }
    }

    protected def register(index: ActorIndex, data: ActorData): Unit

    final def execute(function: => Any): Unit = execute((() => function): Runnable)

    override def reportFailure(cause: Throwable): Unit = ??? // TODO:

    override def execute(command: Runnable): Unit = executorService.execute(command)
}
