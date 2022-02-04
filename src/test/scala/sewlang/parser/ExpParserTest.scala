package sewlang.parser

import org.scalatest.funsuite.AnyFunSuite
import sexpr.ast._
import sewlang.ast._
import sewlang.exception.ExpParserException
import sewlang.util.ExpTestUtilities

class ExpParserTest extends AnyFunSuite with ExpTestUtilities {

  test("Exp Parsing Numbers") {
    assert(ExpParser(SNum(3)) == NumberExp(3))
    assert(ExpParser(SNum(5.2)) == NumberExp(5.2))
    assert(ExpParser(SNum(72.84)) == NumberExp(72.84))

    assert(ExpParser(parseSExpr("23.5")) == NumberExp(23.5))
  }

  test("Exp Parsing Strings") {
    assert(ExpParser(SString("test")) == StringExp("test"))
    assert(ExpParser(SString("Hello World!")) == StringExp("Hello World!"))
    assert(ExpParser(SString("")) == StringExp(""))

    assert(ExpParser(parseSExpr("\"x\"")) == StringExp("x"))
  }

  test("Exp Parsing Nil Value") {
    assert(ExpParser(SSym("nil")) == NilExp)
    assert(ExpParser(parseSExpr("nil")) == NilExp)
  }

  test("Exp Parsing Boolean Values") {
    assert(ExpParser(SSym("true")) == TrueExp)
    assert(ExpParser(SSym("false")) == FalseExp)

    assert(ExpParser(parseSExpr("false")) == FalseExp)
  }

  test("Exp Parsing Valid Identifiers") {
    assert(ExpParser(SSym("test")) == IdExp("test"))
    assert(ExpParser(SSym("X2")) == IdExp("X2"))
    assert(ExpParser(SSym("x_Teste")) == IdExp("x_Teste"))

    assert(ExpParser(parseSExpr("firstValue")) == IdExp("firstValue"))
  }

  test("Exp Parsing Invalid Identifiers") {
    var caught =
      intercept[ExpParserException] {
        ExpParser(SSym("_teste"))
      }
    assert(caught.getMessage.contains("_teste"))

    caught =
      intercept[ExpParserException] {
        ExpParser(SSym("0_teste"))
      }
    assert(caught.getMessage.contains("0_teste"))

    caught =
      intercept[ExpParserException] {
        ExpParser(parseSExpr("_123x"))
      }
    assert(caught.getMessage.contains("_123x"))
  }

  test("Exp Parsing Sum Expression") {
    val input1 = SList(List(SSym("+"), SNum(3), SSym("x"))) // expression (+ 3 x)
    val expected1 = SumExp(NumberExp(3), IdExp("x"))
    assert(ExpParser(input1) == expected1)

    val input2 = SList(List(SSym("+"), SList(List(SSym("+"), SNum(1), SNum(1))), SNum(3))) // expression (+ (+ 1 1) 3)
    val expected2 = SumExp(SumExp(NumberExp(1), NumberExp(1)), NumberExp(3))
    assert(ExpParser(input2) == expected2)

    assert(ExpParser(parseSExpr("(+ 2 x)")) == SumExp(NumberExp(2), IdExp("x")))
  }

  test("Exp Parsing Multiplication Expression") {
    val input1 = SList(List(SSym("*"), SSym("x"), SNum(3.2))) // expression (* x 3.2)
    val expected1 = MultExp(IdExp("x"), NumberExp(3.2))
    assert(ExpParser(input1) == expected1)

    val input2 = SList(List(SSym("*"), SList(List(SSym("+"), SSym("x"), SSym("y"))), SSym("z"))) // expression (* (+ x y) z)
    val expected2 = MultExp(SumExp(IdExp("x"), IdExp("y")), IdExp("z"))
    assert(ExpParser(input2) == expected2)

    assert(ExpParser(parseSExpr("(* (+ x 1) (* y 2))")) == MultExp(SumExp(IdExp("x"), NumberExp(1)), MultExp(IdExp("y"), NumberExp(2))))
  }

  test("Exp Parsing Equal Expression") {
    val input1 = SList(List(SSym("="), SSym("x"), SNum(10))) // expression (= x 10)
    val expected1 = EqualExp(IdExp("x"), NumberExp(10))
    assert(ExpParser(input1) == expected1)

    assert(ExpParser(parseSExpr("(= x 50)")) == EqualExp(IdExp("x"), NumberExp(50)))
  }

  test("Exp Parsing Less Than Expression") {
    val input1 = SList(List(SSym("<"), SSym("x"), SList(List(SSym("+"), SSym("y"), SNum(1))))) // expression (< x (+ y 1))
    val expected1 = LessThanExp(IdExp("x"), SumExp(IdExp("y"), NumberExp(1)))
    assert(ExpParser(input1) == expected1)

    assert(ExpParser(parseSExpr("(< x 10)")) == LessThanExp(IdExp("x"), NumberExp(10)))
  }

  test("Exp Parsing Not Expression") {
    val input1 = SList(List(SSym("not"), SSym("false"))) // expression (not false)
    val expected1 = NotExp(FalseExp)
    assert(ExpParser(input1) == expected1)

    assert(ExpParser(parseSExpr("(not (and (< x 10) true))")) == NotExp(AndExp(LessThanExp(IdExp("x"), NumberExp(10)), TrueExp)))
  }

  test("Exp Parsing And Expression") {
    val input1 = SList(List(SSym("and"), SSym("true"), SSym("false"))) // expression (and true false)
    val expected1 = AndExp(TrueExp, FalseExp)
    assert(ExpParser(input1) == expected1)

    assert(ExpParser(parseSExpr("(and (< x 10) true)")) == AndExp(LessThanExp(IdExp("x"), NumberExp(10)), TrueExp))
  }

  test("Exp Parsing Variable Declaration Expression") {
    val input1 = SList(List(SSym("var"), SSym("x"), SNum(0))) // expression (var x 0)
    val expected1 = VarDeclExp(IdExp("x"), NumberExp(0))
    assert(ExpParser(input1) == expected1)

    assert(ExpParser(parseSExpr("(var continue true)")) == VarDeclExp(IdExp("continue"), TrueExp))
  }

  test("Exp Parsing Variable Assignment Expression") {
    val input1 = SList(List(SSym("set"), SSym("x"), SList(List(SSym("+"), SSym("x"), SNum(1))))) // expression (set x (+ x 1))
    val expected1 = VarAssignExp(IdExp("x"), SumExp(IdExp("x"), NumberExp(1)))
    assert(ExpParser(input1) == expected1)

    assert(ExpParser(parseSExpr("(set continue false)")) == VarAssignExp(IdExp("continue"), FalseExp))
  }

  test("Exp Parsing If Expression") {
    val input1 = SList(List(SSym("if"), SList(List(SSym("="), SSym("x"), SNum(10))), SSym("x"), SNum(1))) // expression (if (= x 10) x 1)
    val expected1 = IfExp(EqualExp(IdExp("x"), NumberExp(10)), IdExp("x"), NumberExp(1))
    assert(ExpParser(input1) == expected1)

    assert(ExpParser(parseSExpr("(if (< a b) a b)")) == IfExp(LessThanExp(IdExp("a"), IdExp("b")), IdExp("a"), IdExp("b")))
  }

  test("Exp Parsing While Expression") {
    val input1 = SList(List(SSym("while"), SList(List(SSym("<"), SSym("x"), SNum(10))), SList(List(SSym("set"), SSym("x"), SList(List(SSym("+"), SSym("x"), SNum(1))))))) // expression (while (< x 10) (set x (+ x 1)))
    val expected1 = WhileExp(LessThanExp(IdExp("x"), NumberExp(10)), VarAssignExp(IdExp("x"), SumExp(IdExp("x"), NumberExp(1))))
    assert(ExpParser(input1) == expected1)

    assert(ExpParser(parseSExpr("(while continue (set continue false))")) == WhileExp(IdExp("continue"), VarAssignExp(IdExp("continue"), FalseExp)))
  }

  test("Exp Parsing Block Expression") {
    val input1 = SList(List(SSym("begin"), SList(List(SSym("var"), SSym("continue"), SSym("true"))), SList(List(SSym("set"), SSym("continue"), SSym("false"))))) // expression (begin (var continue true) (set continue false))
    val expected1 = BlockExp(List(VarDeclExp(IdExp("continue"), TrueExp), VarAssignExp(IdExp("continue"), FalseExp)))
    assert(ExpParser(input1) == expected1)

    val input2 =
      """(begin
            (var x 0)
            (while (< x 10) (set x (+ x 1)))
         )
      """
    val expected2 = BlockExp(List(VarDeclExp(IdExp("x"), NumberExp(0)), WhileExp(LessThanExp(IdExp("x"), NumberExp(10)), VarAssignExp(IdExp("x"), SumExp(IdExp("x"), NumberExp(1))))))
    assert(ExpParser(parseSExpr(input2)) == expected2)
  }

  test("Exp Parsing Print Expression") {
    val input1 = SList(List(SSym("print"), SString("x = "), SSym("x"))) // expression (print "x = " x)
    val expected1 = PrintExp(List(StringExp("x = "), IdExp("x")))
    assert(ExpParser(input1) == expected1)

    val input2 = "(print \"2 + 3 = \" (+ 2 3))"
    val expected2 = PrintExp(List(StringExp("2 + 3 = "), SumExp(NumberExp(2), NumberExp(3))))
    assert(ExpParser(input1) == expected1)
  }

  test("Exp Parsing Read Expression") {
    assert(ExpParser(SList(List(SSym("read-num")))) == ReadNumExp)
    assert(ExpParser(parseSExpr("(var x (read-num))")) == VarDeclExp(IdExp("x"), ReadNumExp))
  }

  test("Exp Parsing Exception") {
    var caught =
      intercept[ExpParserException] {
        ExpParser(parseSExpr("(invalid operation)"))
      }
    assert(caught.getMessage.contains("invalid operation"))

    caught =
      intercept[ExpParserException] {
        ExpParser(parseSExpr("(if (x (mod x 2) 0) x (+ x 1))")) // unimplemented expression mod
      }
    assert(caught.getMessage.contains("mod x 2"))
  }

  test("Exp Parsing Desugared Increment and Sum Augmented Assignment Expressions") {
    assert(ExpParser(parseSExpr("(++ x)")) == VarAssignExp(IdExp("x"), SumExp(IdExp("x"), NumberExp(1))))
    assert(ExpParser(parseSExpr("(set y (++ x))")) == VarAssignExp(IdExp("y"), VarAssignExp(IdExp("x"), SumExp(IdExp("x"), NumberExp(1)))))

    assert(ExpParser(parseSExpr("(+= x 5)")) == VarAssignExp(IdExp("x"), SumExp(IdExp("x"), NumberExp(5))))
    assert(ExpParser(parseSExpr("(+= x (++ y))")) == VarAssignExp(IdExp("x"), SumExp(IdExp("x"), VarAssignExp(IdExp("y"), SumExp(IdExp("y"), NumberExp(1))))))
  }

  test("Exp Parsing Desugared For Expressions") {
    // (for (var x 0) (< x 10) (++ x) (print x))
    val input1 = SList(List(SSym("for"), SList(List(SSym("var"), SSym("x"), SNum(0))), SList(List(SSym("<"), SSym("x"), SNum(10))), SList(List(SSym("++"), SSym("x"))), SList(List(SSym("print"), SSym("x")))))
    // (begin (var x 0) (while (< x 10) (begin (print x) (++ x))))
    val expected1 = BlockExp(List(VarDeclExp(IdExp("x"), NumberExp(0)), WhileExp(LessThanExp(IdExp("x"), NumberExp(10)), BlockExp(List(PrintExp(List(IdExp("x"))), VarAssignExp(IdExp("x"), SumExp(IdExp("x"), NumberExp(1))))))))
    assert(ExpParser(input1) == expected1)

    val input2 = "(for (var i 0) (< i 10) (set i (+ i 1)) (print i))"
    val expected2 = "(begin (var i 0) (while (< i 10) (begin (print i) (set i (+ i 1)))))"
    assert(ExpParser(parseSExpr(input2)) == ExpParser(parseSExpr(expected2)))
  }

}