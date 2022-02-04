package sewlang.parser

import sewlang.util.ExpTestUtilities
import org.scalatest.funsuite.AnyFunSuite
import sexpr.ast._

class ExpDesugarTest extends AnyFunSuite with ExpTestUtilities {

  test("Exp Desugaring Non Sugar Expression") {
    assert(ExpDesugar(parseSExpr("(+ 1 2)")) == parseSExpr("(+ 1 2)"))
    assert(ExpDesugar(parseSExpr("(set x (* x 2))")) == parseSExpr("(set x (* x 2))"))
  }

  test("Exp Desugaring Increment Expression") { // (++ x) --> (set x (+ x 1))
    val input1 = SList(List(SSym("++"), SSym("x"))) // (++ x)
    val expected1 = SList(List(SSym("set"), SSym("x"), SList(List(SSym("+"), SSym("x"), SNum(1))))) // (set x (+ x 1))
    assert(ExpDesugar(input1) == expected1)

    assert(ExpDesugar(parseSExpr("(++ a)")) == parseSExpr("(set a (+ a 1.0))"))
  }

  test("Exp Desugaring Sum Augmented Assignment Expression") { // (+= x y) --> (set x (+ x y))
    val input1 = SList(List(SSym("+="), SSym("x"), SSym("y"))) // (+= x y)
    val expected1 = SList(List(SSym("set"), SSym("x"), SList(List(SSym("+"), SSym("x"), SSym("y"))))) // (set x (+ x y))
    assert(ExpDesugar(input1) == expected1)

    assert(ExpDesugar(parseSExpr("(+= a 5)")) == parseSExpr("(set a (+ a 5.0))"))
  }

  test("Exp Desugaring For Expression") { // (for (var x 0) (< x 10) (set x (+ x 1)) (print x)) --> (begin (var x 0) (while (< x 10) (begin (print x) (set x (+ x 1)))))
    val input1 = SList(List(SSym("for"), SList(List(SSym("var"), SSym("x"), SNum(0))), SList(List(SSym("<"), SSym("x"), SNum(10))), SList(List(SSym("set"), SSym("x"), SList(List(SSym("+"), SSym("x"), SNum(1))))), SList(List(SSym("print"), SSym("x")))))
    val expected1 = SList(List(
      SSym("begin"),
      SList(List(SSym("var"), SSym("x"), SNum(0))),
      SList(List(SSym("while"), SList(List(SSym("<"), SSym("x"), SNum(10))),
        SList(List(
          SSym("begin"),
          SList(List(SSym("print"), SSym("x"))),
          SList(List(SSym("set"), SSym("x"), SList(List(SSym("+"), SSym("x"), SNum(1)))))))))))

    assert(ExpDesugar(input1) == expected1)

    val input2 = "(for (set a 50) (not (= a 0)) (set a (- a 1)) (print a))"
    val expected2 =
      """
        (begin
          (set a 50)
          (while (not (= a 0))
            (begin
              (print a)
              (set a (- a 1))
            )
          )
        )
      """
    assert(ExpDesugar(parseSExpr(input2)) == parseSExpr(expected2))
  }

}