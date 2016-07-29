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

    private def hasImplicitView(from: Type, to: Type): Boolean = c.inferImplicitView(q"null", from, to) ne EmptyTree

    private def result[T]: Expr[T] = c.Expr[T](q"null")

    private def weakSubset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: Boolean = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        firstTypes.forall(tpe => secondTypes.exists(tpe <:< _))
    }

    private def isMemberExists[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: Boolean = {
        val tpe = weakTypeOf[T]
        val unionTypes = decay[U]
        unionTypes.contains(tpe)
    }

    private def isWeakMemberExists[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: Boolean = {
        val tpe = weakTypeOf[T]
        val unionTypes = decay[U]

        unionTypes.exists(tpe <:< _) || unionTypes.exists(hasImplicitView(tpe, _))
    }

    def isMember[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T ∈ U] = {
        if (!isMemberExists[T, U]) stop()
        result[T ∈ U]
    }

    def isWeakMember[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T weak_∈ U] = {
        if (!isWeakMemberExists[T, U]) stop()
        result[T weak_∈ U]
    }

    def isNotAMember[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T ∉ U] = {
        if (isMemberExists[T, U]) stop()
        result[T ∉ U]
    }

    def isNotAWeakMember[T : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T weak_∉ U] = {
        if (isWeakMemberExists[T, U]) stop()
        result[T weak_∉ U]
    }

    def equals[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[union.=:=[T, U]] = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        if (firstTypes != secondTypes) stop()
        result[union.=:=[T, U]]
    }

    def notEquals[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[union.=!=[T, U]] = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        if (firstTypes != secondTypes) result[union.=!=[T, U]]
        else                           stop()
    }

    def isSubset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T ⊂ U] = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        if (firstTypes.subsetOf(secondTypes)) result[T ⊂ U]
        else                                  stop()
    }

    def isWeakSubset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T weak_⊂ U] = {
        if (weakSubset[T, U]) result[T weak_⊂ U]
        else                  stop()
    }

    def isSuperset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T ⊃ U] = {
        val firstTypes = decay[T]
        val secondTypes = decay[U]

        if (secondTypes.subsetOf(firstTypes)) result[T ⊃ U]
        else                                  stop()
    }

    def isWeakSuperset[T <: Union : c.WeakTypeTag, U <: Union : c.WeakTypeTag]: c.Expr[T weak_⊃ U] = {
        if (weakSubset[U, T]) result[T weak_⊃ U]
        else                  stop()
    }
}
