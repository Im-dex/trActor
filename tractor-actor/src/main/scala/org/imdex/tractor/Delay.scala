package org.imdex.tractor

import scala.concurrent.duration.FiniteDuration

/**
  * Created by a.tsukanov on 21.07.2016.
  */
final case class Delay(duration: FiniteDuration) extends AnyVal
