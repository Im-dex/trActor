package org.imdex.tractor.union

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

import scala.language.implicitConversions

object UnionSpec {
    private sealed trait Base
    private final class TestClass extends Base

    private final class ImplicitTest
    private final class ImplicitTest2
    private implicit def ImplicitTest2String(value: ImplicitTest): String = ""
}

/**
  * Created by a.tsukanov on 14.07.2016.
  */
//noinspection ScalaUnusedSymbol
@RunWith(classOf[JUnitRunner])
class UnionSpec extends FlatSpec with Matchers {
    import UnionSpec._

    type Alias = Int
    private type TestUnion = Int | String | TestClass | Int | Alias | List[Float]
    private type WeakTestUnion = Alias | String | Base | Seq[Float]

    "Type union" should "provide `is member` compile type check" in {
        def check(implicit ev: Alias ∈ TestUnion): Unit = ()
        def check2(implicit ev: List[Float] ∈ TestUnion): Unit = ()
        def check3(implicit ev: List[Float] ∈ WeakTestUnion): Unit = ()
        def checkEmpty(implicit ev: Int ∈ ∅): Unit = ()

        "check" should compile
        "check2" should compile
        "check3" shouldNot compile
        "checkEmpty" shouldNot compile
    }

    it should "provide `is not a member` compile time check" in {
        def check(implicit ev: Any ∉ TestUnion): Unit = ()
        def check2(implicit ev: List[Int] ∉ TestUnion): Unit = ()
        def check3(implicit ev: TestClass ∉ TestUnion): Unit = ()
        def checkEmpty(implicit ev: Int ∉ ∅): Unit = ()

        "check" should compile
        "check2" should compile
        "check3" shouldNot compile
        "checkEmpty" should compile
    }

    it should "provide weak `is member` compile type check" in {
        def check(implicit ev: Alias weak_∈ TestUnion): Unit = ()
        def check2(implicit ev: List[Float] weak_∈ TestUnion): Unit = ()
        def checkEmpty(implicit ev: Int weak_∈ ∅): Unit = ()

        def weakCheck(implicit ev: TestClass weak_∈ WeakTestUnion): Unit = ()
        def weakCheck2(implicit ev: List[Float] weak_∈ WeakTestUnion): Unit = ()
        def implicitWeakCheck(implicit ev: ImplicitTest weak_∈ WeakTestUnion): Unit = ()
        def implicitWeakCheck2(implicit ev: ImplicitTest2 weak_∈ WeakTestUnion): Unit = ()

        "check" should compile
        "check2" should compile
        "checkEmpty" shouldNot compile

        "weakCheck" should compile
        "weakCheck2" should compile
        "implicitWeakCheck" should compile
        "implicitWeakCheck2" shouldNot compile
    }

    it should "provide weak `is not a member` compile type check" in {
        def check(implicit ev: Any weak_∉ TestUnion): Unit = ()
        def check2(implicit ev: List[Int] weak_∉ TestUnion): Unit = ()
        def check3(implicit ev: TestClass weak_∉ TestUnion): Unit = ()
        def checkEmpty(implicit ev: Int weak_∉ ∅): Unit = ()

        def weakCheck(implicit ev: TestClass weak_∉ WeakTestUnion): Unit = ()
        def weakCheck2(implicit ev: List[Float] weak_∉ WeakTestUnion): Unit = ()
        def implicitWeakCheck(implicit ev: ImplicitTest weak_∉ WeakTestUnion): Unit = ()
        def implicitWeakCheck2(implicit ev: ImplicitTest2 weak_∉ WeakTestUnion): Unit = ()

        "check" should compile
        "check2" should compile
        "check3" shouldNot compile
        "checkEmpty" should compile

        "weakCheck" shouldNot compile
        "weakCheck2" shouldNot compile
        "implicitWeakCheck" shouldNot compile
        "implicitWeakCheck2" should compile
    }

    // TODO: rest of tests
}
