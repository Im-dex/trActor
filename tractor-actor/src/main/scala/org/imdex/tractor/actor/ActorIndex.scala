package org.imdex.tractor.actor

/**
  * Created by a.tsukanov on 29.07.2016.
  */
final class ActorIndex(private val index: Int) extends AnyVal

object ActorIndex {
    def apply(index: Int): ActorIndex = new ActorIndex(index)
}
