package org.imdex.tractor.meta

import org.imdex.tractor
import org.imdex.tractor.The

import scala.annotation.tailrec
import scala.reflect.macros.whitebox

/**
  * Created by a.tsukanov on 26.07.2016.
  */
private[tractor] class ActorFactoryMacros(val c: whitebox.Context) {
    import c.universe._

    def spawn[Messages : WeakTypeTag](the: Expr[The[_]], args: Expr[Any]*): Tree = {
        val actualType = the.actualType
        if (actualType.typeArgs.isEmpty) c.abort(c.enclosingPosition, s"Invalid actor type ${actualType.typeSymbol.fullName}")

        val `type` = actualType.typeArgs.head
        val argTypes = args.map(_.actualType)

        val constructors = `type`.decls.collect{ case decl if decl.isConstructor => decl.asMethod }

        @tailrec
        def check(args: Seq[Type], ctorArgs: List[Type]): Boolean = {
            if (args.isEmpty && ctorArgs.isEmpty)                                                true
            else if ((args.isEmpty && ctorArgs.nonEmpty) || (args.nonEmpty && ctorArgs.isEmpty)) false
            else {
                if (args.head weak_<:< ctorArgs.head) check(args.tail, ctorArgs.tail)
                else                                  false
            }
        }

        val constructor = constructors.find { ctor =>
            val args = ctor.paramLists.flatten.map(_.typeSignature) // TODO: optimize with lazy iteration
            check(argTypes, args)
        }.getOrElse(c.abort(c.enclosingPosition, "Suitable constructor not found"))

        val params = {
            val paramLists = constructor.paramLists

            import tractor.util._

            if (paramLists.tail.isEmpty) args :: Nil
            else                         (args: Seq[Expr[Any]]).split(paramLists.map(_.length): _*)
        }

        q"""val creator = new ActorInstanceCreator[${`type`}] {
            override def create: ${`type`} = new ${`type`}(...$params)
        }
        spawn[${weakTypeOf[Messages]}, ${`type`}](creator)"""
    }
}
