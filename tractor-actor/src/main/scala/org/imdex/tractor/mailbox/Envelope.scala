package org.imdex.tractor.mailbox

import org.imdex.tractor.actor.JustRef

/**
  * Created by a.tsukanov on 16.07.2016.
  */
final case class Envelope(message: Any, sender: JustRef)
