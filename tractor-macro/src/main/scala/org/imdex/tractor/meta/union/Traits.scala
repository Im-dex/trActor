package org.imdex.tractor.meta.union

import org.imdex.tractor.union
import org.imdex.tractor.union._

import scala.reflect.macros.whitebox

/**
  * Created by a.tsukanov on 14.07.2016.
  */
class Traits(val traitsCtx: whitebox.Context) extends Common(traitsCtx) {
    import c.universe._

    private def stop(): Nothing = c.abort(c.enclosingPosition, "")

    private def weakSubset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: Boolean = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        firstTypes.forall(tpe => secondTypes.exists(tpe <:< _))
    }

    def isMember[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T ∈ U] = {
        val tpe = weakTypeOf[T]
        val unionTypes = decay[U]

        if (unionTypes.contains(tpe)) c.Expr[T ∈ U](q"null")
        else                          stop()
    }

    def isWeakMember[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T weak_∈ U] = {
        val tpe = weakTypeOf[T]
        val unionTypes = decay[U]

        if (unionTypes.exists(tpe <:< _)) c.Expr[T weak_∈ U](q"null")
        else                              stop()
    }

    def isNotAWeakMember[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T weak_∉ U] = {
        val tpe = weakTypeOf[T]
        val unionTypes = decay[U]

        if (unionTypes.exists(tpe <:< _)) stop()
        else                              c.Expr[T weak_∉ U](q"null")
    }

    def isNotAMember[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T ∉ U] = {
        val tpe = weakTypeOf[T]
        val unionTypes = decay[U]

        if (unionTypes.contains(tpe)) stop()
        c.Expr[T ∉ U](q"null")
    }

    def equals[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[union.=:=[T, U]] = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        if (firstTypes != secondTypes) stop()
        c.Expr[union.=:=[T, U]](q"null")
    }

    def notEquals[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[union.=!=[T, U]] = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        if (firstTypes != secondTypes) c.Expr[union.=!=[T, U]](q"null")
        else                           stop()
    }

    def isSubset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T ⊂ U] = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        if (firstTypes.subsetOf(secondTypes)) c.Expr[T ⊂ U](q"null")
        else                                  stop()
    }

    def isWeakSubset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T weak_⊂ U] = {
        if (weakSubset[T, U]) c.Expr[T weak_⊂ U](q"null")
        else                  stop()
    }

    def isSuperset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T ⊃ U] = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        if (secondTypes.subsetOf(firstTypes)) c.Expr[T ⊃ U](q"null")
        else                                  stop()
    }

    def isWeakSuperset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T weak_⊃ U] = {
        if (weakSubset[U, T]) c.Expr[T weak_⊃ U](q"null")
        else                  stop()
    }
}
