package org.imdex.tractor.actor

import org.imdex.tractor.{Delay, Response, Sender, Timeout}
import org.imdex.tractor.union.{Union, weak_∈, |, ∅}

import scala.concurrent.Future

object Ref {
    type Of[T] = Ref[T | ∅]

    val BlackHole: Ref.Of[Any] = null // TODO: replace null by black hole actor ref
}

/**
  * Created by a.tsukanov on 21.07.2016.
  */
sealed trait Ref[Messages <: Union] {
    //
    // send
    //

    def send[T](message: T, sender: Ref[_] = Ref.BlackHole)(implicit ev: T weak_∈ Messages): Unit

    final def send[T, R](message: T with Response[R], sender: Ref.Of[R])(implicit ev: T weak_∈ Messages,
                                                                         dummy: DummyImplicit): Unit = send[T](message, sender)

    final def send[T, S <: Union](message: T with Sender[S], sender: Ref[S])(implicit ev: T weak_∈ Messages,
                                                                             dummy: DummyImplicit,
                                                                             dummy2: DummyImplicit): Unit = send[T](message, sender)

    final def send[T, R, S <: Union](message: T with Response[R] with Sender[S], sender: Ref[S | R])(implicit ev: T weak_∈ Messages,
                                                                                                     dummy: DummyImplicit,
                                                                                                     dummy2: DummyImplicit,
                                                                                                     dummy3: DummyImplicit): Unit = send[T](message, sender)

    final def ![T](message: T)(implicit ev: T weak_∈ Messages,
                               sender: Ref[_] = Ref.BlackHole): Unit = send[T](message, sender)

    final def ![T, R](message: T with Response[R])(implicit ev: T weak_∈ Messages,
                                                   sender: Ref.Of[R],
                                                   dummy: DummyImplicit): Unit = send[T, R](message, sender)

    final def ![T, S <: Union](message: T with Sender[S])(implicit ev: T weak_∈ Messages,
                                                          sender: Ref[S],
                                                          dummy: DummyImplicit,
                                                          dummy2: DummyImplicit): Unit = send[T, S](message, sender)(ev, dummy, dummy2)

    final def ![T, R, S <: Union](message: T with Response[R] with Sender[S])(implicit ev: T weak_∈ Messages,
                                                                              sender: Ref[S | R],
                                                                              dummy: DummyImplicit,
                                                                              dummy2: DummyImplicit,
                                                                              dummy3: DummyImplicit): Unit = {
        send[T, R, S](message, sender)(ev, dummy, dummy2, dummy3)
    }

    //
    // delayedSend
    //

    def delayedSend[T](message: T, delay: Delay, sender: Ref[_] = Ref.BlackHole)(implicit ev: T weak_∈ Messages): Unit

    final def delayedSend[T, R](message: T with Response[R], delay: Delay, sender: Ref.Of[R])(implicit ev: T weak_∈ Messages,
                                                                                              dummy: DummyImplicit): Unit = {
        delayedSend[T](message, delay, sender)
    }

    final def delayedSend[T, S <: Union](message: T with Sender[S], delay: Delay, sender: Ref[S])(implicit ev: T weak_∈ Messages,
                                                                                                  dummy: DummyImplicit,
                                                                                                  dummy2: DummyImplicit): Unit = {
        delayedSend[T](message, delay, sender)
    }

    final def delayedSend[T, R, S <: Union](message: T with Response[R] with Sender[S], delay: Delay, sender: Ref[S | R])(implicit ev: T weak_∈ Messages,
                                                                                                                          dummy: DummyImplicit,
                                                                                                                          dummy2: DummyImplicit,
                                                                                                                          dummy3: DummyImplicit): Unit = {
        delayedSend[T](message, delay, sender)
    }

    final def ~![T](message: T)(implicit ev: T weak_∈ Messages,
                                delay: Delay,
                                sender: Ref[_] = Ref.BlackHole): Unit = delayedSend[T](message, delay, sender)

    final def ~![T, R](message: T with Response[R])(implicit ev: T weak_∈ Messages,
                                                    delay: Delay,
                                                    sender: Ref.Of[R],
                                                    dummy: DummyImplicit): Unit = delayedSend[T, R](message, delay, sender)

    final def ~![T, S <: Union](message: T with Sender[S])(implicit ev: T weak_∈ Messages,
                                                           delay: Delay,
                                                           sender: Ref[S],
                                                           dummy: DummyImplicit,
                                                           dummy2: DummyImplicit): Unit = delayedSend[T, S](message, delay, sender)(ev, dummy, dummy2)

    final def ~![T, R, S <: Union](message: T with Response[R] with Sender[S])(implicit ev: T weak_∈ Messages,
                                                                               delay: Delay,
                                                                               sender: Ref[S | R],
                                                                               dummy: DummyImplicit,
                                                                               dummy2: DummyImplicit,
                                                                               dummy3: DummyImplicit): Unit = {
        delayedSend[T, R, S](message, delay, sender)(ev, dummy, dummy2, dummy3)
    }

    //
    // ask
    //

    def ask[T, R](message: T with Response[R],
                  timeout: Timeout,
                  sender: Ref[_] = Ref.BlackHole)(implicit ev: T weak_∈ Messages): Future[R]

    final def ask[T, R, S <: Union](message: T with Response[R] with Sender[S],
                                    timeout: Timeout,
                                    sender: Ref[S])(implicit ev: T weak_∈ Messages, dummy: DummyImplicit): Future[R] = ask[T, R](message, timeout, sender)

    final def ?[T, R](message: T with Response[R])(implicit ev: T weak_∈ Messages,
                                                   timeout: Timeout,
                                                   sender: Ref[_] = Ref.BlackHole): Future[R] = ask[T, R](message, timeout, sender)

    final def ?[T, R, S <: Union](message: T with Response[R] with Sender[S])(implicit ev: T weak_∈ Messages,
                                                                              timeout: Timeout,
                                                                              sender: Ref[S],
                                                                              dummy: DummyImplicit): Future[R] = ask[T, R, S](message, timeout, sender)

    //
    // delayedAsk
    //

    def delayedAsk[T, R](message: T with Response[R],
                         timeout: Timeout,
                         delay: Delay,
                         sender: Ref[_] = Ref.BlackHole)(implicit ev: T weak_∈ Messages): Future[R]

    final def delayedAsk[T, R, S <: Union](message: T with Response[R] with Sender[S],
                                           timeout: Timeout,
                                           delay: Delay,
                                           sender: Ref[S])(implicit ev: T weak_∈ Messages, dummy: DummyImplicit): Future[R] = {
        delayedAsk[T, R](message, timeout, delay, sender)
    }
}
