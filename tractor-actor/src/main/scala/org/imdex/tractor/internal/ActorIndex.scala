package org.imdex.tractor.internal

/**
  * Created by a.tsukanov on 29.07.2016.
  */
private[tractor] final class ActorIndex(private val index: Int) extends AnyVal

private[tractor] object ActorIndex {
    def apply(index: Int): ActorIndex = new ActorIndex(index)
}
