package org.imdex.tractor.util

import scala.concurrent.duration.FiniteDuration

/**
  * Created by a.tsukanov on 21.07.2016.
  */
final case class Timeout(duration: FiniteDuration) extends AnyVal
