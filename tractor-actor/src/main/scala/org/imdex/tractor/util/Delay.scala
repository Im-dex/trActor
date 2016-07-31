package org.imdex.tractor.util

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

/**
  * Created by a.tsukanov on 21.07.2016.
  */
final case class Delay(duration: FiniteDuration) extends AnyVal

object Delay {
    implicit def toDuration(delay: Delay): FiniteDuration = delay.duration
}