package org.imdex.tractor.meta

import java.util.Objects

import scala.annotation.tailrec
import scala.language.implicitConversions
import scala.reflect.macros.whitebox
import scala.collection.mutable

/**
  * Created by a.tsukanov on 14.07.2016.
  */
private[meta] class Macros[C <: whitebox.Context](val c: C) {
    import c.universe._

    sealed case class TypeWrapper(tpe: Type) {
        override def equals(obj: scala.Any): Boolean = obj match {
            case TypeWrapper(that) => tpe =:= that
            case that: Type        => tpe =:= that
            case _                 => false
        }

        override def canEqual(that: Any) = that match {
            case _: Type | _: TypeWrapper => true
            case _                        => false
        }

        override def hashCode(): Int = TypeWrapper.hash(tpe :: Nil)
    }

    object TypeWrapper {
        @tailrec
        private def hash(types: Seq[Type], buffer: mutable.ListBuffer[Type] = mutable.ListBuffer.empty[Type]): Int = {
            if (types.isEmpty) {
                Objects.hash(buffer: _*)
            } else {
                val `type` = types.head.dealias
                hash(types.tail, buffer ++= `type`.typeArgs)
            }
        }

        implicit def Type2Wrapper(tpe: Type): TypeWrapper = TypeWrapper(tpe)

        implicit def Wrapper2Type(wrapper: TypeWrapper): Type = wrapper.tpe
    }
}
