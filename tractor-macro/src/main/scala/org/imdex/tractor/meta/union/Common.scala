package org.imdex.tractor.meta.union

import org.imdex.tractor.meta.Macros
import org.imdex.tractor.union.{Union, |, ∅}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.macros.whitebox

/**
  * Created by a.tsukanov on 14.07.2016.
  */
private[union] class Common[C <: whitebox.Context](val commonCtx: C) extends Macros(commonCtx) {
    import c.universe._

    private[this] val UnionType = typeOf[_ | _]
    private[this] val EmptyType = typeOf[∅]

    def decay[T <: Union : c.WeakTypeTag]: Set[TypeWrapper] = decay(weakTypeOf[T])

    def decay(unionType: Type): Set[TypeWrapper] = {
        @tailrec
        def decay(types: List[Type], buffer: mutable.Builder[TypeWrapper, Set[TypeWrapper]] = Set.newBuilder[TypeWrapper]): Set[TypeWrapper] = {
            if (types.isEmpty) {
                buffer.result()
            } else {
                val tpe = types.head

                if (tpe =:= EmptyType)              decay(types.tail, buffer)
                else if (tpe.erasure =:= UnionType) decay(types.tail ++ tpe.dealias.typeArgs, buffer)
                else                                decay(types.tail, buffer += tpe)
            }
        }

        decay(unionType :: Nil)
    }
}
