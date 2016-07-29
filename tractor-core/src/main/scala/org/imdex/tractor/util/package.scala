package org.imdex.tractor

import scala.annotation.tailrec
import scala.collection.{TraversableLike, mutable}
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

/**
  * Created by a.tsukanov on 29.07.2016.
  */
package object util {
    implicit class RichTractorTraversableLike[T, Collection[X] <: TraversableLike[X, Collection[X]]](val collection: Collection[T]) extends AnyVal {
        def split(lengths: Int*)(implicit cbf: CanBuildFrom[Collection[T], Collection[T], Collection[Collection[T]]]): Collection[Collection[T]] = {
            @tailrec
            def split(lengths: Seq[Int], offset: Int = 0, builder: mutable.Builder[Collection[T], Collection[Collection[T]]] = cbf()): Collection[Collection[T]] = {
                if (lengths.isEmpty) {
                    val trailingPart = collection.splitAt(offset)._2
                    if (trailingPart.nonEmpty) builder += trailingPart
                    builder.result()
                } else {
                    val length = lengths.head
                    val nextOffset = offset + length
                    builder += collection.slice(offset, nextOffset)
                    split(lengths.tail, nextOffset, builder)
                }
            }

            split(lengths)
        }
    }
}
