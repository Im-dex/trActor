package org.imdex.tractor

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by a.tsukanov on 21.07.2016.
  */
trait Response[T] extends Any with Serializable {
    def reply(value: T): Unit
    def reply(throwable: Throwable): Unit
    def reply(`try`: Try[T]): Unit
    def reply(future: Future[T]): Unit
}
