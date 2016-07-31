package org.imdex.tractor.util

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

/**
  * Created by a.tsukanov on 21.07.2016.
  */
final case class Timeout(duration: FiniteDuration) extends AnyVal

object Timeout {
    implicit def toDuration(timeout: Timeout): FiniteDuration = timeout.duration
}
