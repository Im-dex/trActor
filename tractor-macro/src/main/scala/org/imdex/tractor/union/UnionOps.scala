package org.imdex.tractor.union

/**
  * Created by a.tsukanov on 14.07.2016.
  */
private[tractor] trait UnionOps {
    type UNil = ∅

    type memberOf[T, U <: Union]     = T ∈ U
    type notAMemberOf[T, U <: Union] = T ∉ U

    type weakMemberOf[T, U <: Union]     = T weak_∈ U
    type notAWeakMemberOf[T, U <: Union] = T weak_∉ U

    type equals[T <: Union, U <: Union]    = T =:= U
    type notEquals[T <: Union, U <: Union] = T =!= U

    type subsetOf[T <: Union, U <: Union]   = T ⊂ U
    type supersetOf[T <: Union, U <: Union] = T ⊃ U

    type ∪[T <: Union, U <: Union]  = T | U
    type ++[T <: Union, U <: Union] = T ∪ U
    // TODO: type ⋂ = ??? impossible now
}
