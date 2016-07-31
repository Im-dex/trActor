package org.imdex.tractor

import org.imdex.tractor.union.Union

/**
  * Created by a.tsukanov on 31.07.2016.
  */
package object actor {
    private[tractor] type JustActor = Actor[_ <: Union]
    private[tractor] type JustRef = Ref[_ <: Union]
}
