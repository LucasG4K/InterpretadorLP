package sexpr.parser

import org.scalatest.funsuite.AnyFunSuite
import sexpr.ast._
import sexpr.exception.SExprParserException

class SExprParserTest extends AnyFunSuite {

  test("SExpr Parsing Numbers") {
    assert(SExprParser("2.3") == SNum(2.3))
    assert(SExprParser("5") == SNum(5))
    assert(SExprParser("-72.401") == SNum(-72.401))
    assert(SExprParser("+25.23") == SNum(25.23))
  }

  test("SExpr Parsing Strings") {
    assert(SExprParser("\"test\"") == SString("test"))
    assert(SExprParser("\"Hello World!\"") == SString("Hello World!"))
    assert(SExprParser("\"\"") == SString(""))
  }

  test("SExpr Parsing Symbols") {
    assert(SExprParser("+") == SSym("+"))
    assert(SExprParser(">=") == SSym(">="))
    assert(SExprParser("print") == SSym("print"))
    assert(SExprParser("if") == SSym("if"))
  }

  test("SExpr Parsing List") {
    assert(SExprParser("(+ 1 2)") == SList(List(SSym("+"), SNum(1), SNum(2))))
    assert(SExprParser("(var x 0)") == SList(SSym("var") :: SSym("x") :: SNum(0) :: Nil))
    assert(SExprParser("(if (> x y) x y)") == SList(SSym("if") :: SList(SSym(">") :: SSym("x") :: SSym("y") :: Nil) :: SSym("x") :: SSym("y") :: Nil))
    assert(SExprParser("(while (> x 0) (set x (- x 1)))") == SList(List(SSym("while"), SList(List(SSym(">"), SSym("x"), SNum(0))), SList(List(SSym("set"), SSym("x"), SList(List(SSym("-"), SSym("x"), SNum(1))))))))
  }

  test("SExpr Parsing Error") {
    assertThrows[SExprParserException](SExprParser("(+ 1 2"))
    assertThrows[SExprParserException](SExprParser("(1x"))
  }

  test("SExpr List Parsing") {
    assert(SExprParser.parseList("2.3 \"test\" (+ 1 2)") == List(SNum(2.3), SString("test"), SList(List(SSym("+"), SNum(1), SNum(2)))))
  }

}