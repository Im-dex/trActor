package org.imdex.tractor.actor

import java.util.concurrent.{Executors, ScheduledExecutorService}

import org.imdex.tractor.mailbox.{Mailbox, UnboundedMailbox}

/**
  * Created by a.tsukanov on 31.07.2016.
  */
class Environment {
    private[tractor] val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    def deadMessages: Ref.Of[Any] = null // TODO:

    private[tractor] val deadMessagesMailbox: Mailbox = UnboundedMailbox.newMpscMailbox()
}
