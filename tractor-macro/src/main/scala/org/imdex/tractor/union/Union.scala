package org.imdex.tractor.union

import org.imdex.tractor.macros.union.Traits

import scala.annotation.implicitNotFound
import scala.language.experimental.macros

/**
  * Created by a.tsukanov on 14.07.2016.
  */
sealed trait Union

final class |[H, T] extends Union
final class ∅ extends Union
final class Class[T](implicit ev: T <:< Int)

@implicitNotFound("Type ${T} is not a member of union type ${U}")
sealed trait ∈[T, U <: Union]
object ∈ {
    implicit def materialize[T, U <: Union]: T ∈ U = macro Traits.isMember[T, U]
}

@implicitNotFound("Type ${T} is a member of union type ${U}")
sealed trait ∉[T, U <: Union]
object ∉ {
    implicit def empty[T]: T ∉ ∅ = null
    implicit def materialize[T, U <: Union]: T ∉ U = macro Traits.isNotAMember[T, U]
}

@implicitNotFound("Type ${T} is not a weak member of union type ${U}")
sealed trait weak_∈[T, U <: Union]
object weak_∈ {
    implicit def any[U <: Union]: Any weak_∈ U = null
    implicit def materialize[T, U <: Union]: T weak_∈ U = macro Traits.isWeakMember[T, U]
}

@implicitNotFound("Type ${T} is a weak member of union type ${U}")
sealed trait weak_∉[T, U <: Union]
object weak_∉ {
    implicit def empty[T]: T weak_∉ ∅ = null
    implicit def materialize[T, U <: Union]: T weak_∉ U = macro Traits.isNotAWeakMember[T, U]
}

@implicitNotFound("Union type ${T} is not equals to union type ${U}")
sealed trait =:=[T <: Union, U <: Union]
object =:= {
    implicit val empty: ∅ =:= ∅ = null
    implicit def materialize[T <: Union, U <: Union]: T =:= U = macro Traits.equals[T, U]
}

@implicitNotFound("Union type ${T} is equals to union type ${U}")
sealed trait =!=[T <: Union, U <: Union]
object =!= {
    implicit def materialize[T <: Union, U <: Union]: T =!= U = macro Traits.notEquals[T, U]
}

@implicitNotFound("Union type ${T} is not a subset of union type ${U}")
sealed trait ⊂[T <: Union, U <: Union]
object ⊂ {
    implicit val empty: ∅ ⊂ ∅ = null
    implicit def materialize[T <: Union, U <: Union]: T ⊂ U = macro Traits.isSubset[T, U]
}

@implicitNotFound("Union type ${T} is not a weak subset of union type ${U}")
sealed trait weak_⊂[T <: Union, U <: Union]
object weak_⊂ {
    implicit val empty: ∅ weak_⊂ ∅ = null
    implicit def materialize[T <: Union, U <: Union]: T weak_⊂ U = macro Traits.isWeakSubset[T, U]
}

@implicitNotFound("Union type ${T} is not a superset of union type ${U}")
sealed trait ⊃[T <: Union, U <: Union]
object ⊃ {
    implicit val empty: ∅ ⊃ ∅ = null
    implicit def materialize[T <: Union, U <: Union]: T ⊃ U = macro Traits.isSuperset[T, U]
}

@implicitNotFound("Union type ${T} is not a weak superset of union type ${U}")
sealed trait weak_⊃[T <: Union, U <: Union]
object weak_⊃ {
    implicit val empty: ∅ weak_⊃ ∅ = null
    implicit def materialize[T <: Union, U <: Union]: T weak_⊃ U = macro Traits.isWeakSuperset[T, U]
}