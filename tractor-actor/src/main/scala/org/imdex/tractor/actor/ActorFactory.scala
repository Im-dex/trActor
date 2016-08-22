package org.imdex.tractor.actor

import java.util.concurrent.atomic.AtomicInteger

import org.imdex.tractor.ActorConf
import org.imdex.tractor.dispatch.Dispatcher
import org.imdex.tractor.mailbox.Mailbox
import org.imdex.tractor.meta.ActorFactoryMacros
import org.imdex.tractor.union.Union

import scala.language.experimental.macros

final class Conf[T <: JustActor] private(val dispatcher: Dispatcher, val mailbox: Mailbox, val name: String) extends ActorConf[T]

object Conf {
    private[this] val nameIndex = new AtomicInteger(0)

    def apply[T <: JustActor](dispatcher: Dispatcher = null,
                              mailbox: Mailbox = null,
                              name: String = Integer.toHexString(nameIndex.getAndIncrement())): Conf[T] = {
        new Conf[T](dispatcher, mailbox, name)
    }
}

private[tractor] sealed trait ActorInstanceCreator[T <: JustActor] {
    def create: T
    def conf: Conf[T]
}

/**
  * Created by a.tsukanov on 21.07.2016.
  */
private[tractor] trait ActorFactory {
    private[tractor] def spawn[Messages <: Union, T <: JustActor](builder: ActorInstanceCreator[T]): Ref[Messages]

    def spawn[Messages <: Union](conf: ActorConf[_ <: Actor[Messages]], args: Any*): Ref[Messages] = macro ActorFactoryMacros.spawn[Messages]

    // TODO: resolve by name etc
}