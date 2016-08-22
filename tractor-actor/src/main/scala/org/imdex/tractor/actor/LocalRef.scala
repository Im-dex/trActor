package org.imdex.tractor.actor

import org.imdex.tractor.Response
import org.imdex.tractor.mailbox.Envelope
import org.imdex.tractor.union.{Union, weak_∈}
import org.imdex.tractor.util.{Delay, Timeout}

import scala.concurrent.{Future, Promise}

private[tractor] object LocalRef {
    def apply[U <: Union](context: ActorContext): LocalRef[U] = new LocalRef[U](context)
}

/**
  * Created by a.tsukanov on 29.07.2016.
  */
private[tractor] final class LocalRef[Messages <: Union](context: ActorContext) extends Ref[Messages] {
    private def scheduler = context.environment.scheduler

    private def scheduleTimeout(promise: Promise[_], message: Any, sender: JustRef, timeout: Timeout): Unit = {
        val command: Runnable = () => promise.tryFailure(new AskTimeoutException(message, sender, timeout))
        scheduler.schedule(command, timeout.length, timeout.unit)
    }

    override private[tractor] def copyAs[U <: Union]: Ref[U] = this.asInstanceOf[Ref[U]]

    override def send[T](message: T, sender: JustRef = Ref.NoSender)(implicit ev: T weak_∈ Messages): Unit = {
        context.enqueue(Envelope(message, sender))
    }

    override def delayedSend[T](message: T, delay: Delay, sender: JustRef)(implicit ev: T weak_∈ Messages): Unit = {
        val command: Runnable = () => send[T](message, sender)
        scheduler.schedule(command, delay.length, delay.unit)
    }

    override def ask[T, R](message: T with Response[R], timeout: Timeout, sender: JustRef)(implicit ev: T weak_∈ Messages): Future[R] = {
        val promise = Promise[R]
        send[T](message, sender)
        scheduleTimeout(promise, message, sender, timeout)
        promise.future
    }

    override def delayedAsk[T, R](message: T with Response[R], timeout: Timeout, delay: Delay,
                                  sender: JustRef)(implicit ev: T weak_∈ Messages): Future[R] = {
        val promise = Promise[R]
        val command: Runnable = () => {
            send[T](message, sender)
            scheduleTimeout(promise, message, sender, timeout)
        }

        scheduler.schedule(command, delay.length, delay.unit)
        promise.future
    }
}
