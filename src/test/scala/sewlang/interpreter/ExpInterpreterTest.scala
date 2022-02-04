package sewlang.interpreter

import org.scalatest.funsuite.AnyFunSuite
import org.scalamock.scalatest.MockFactory
import sewlang.util.ExpTestUtilities
import sewlang.exception.ExpUnboundVariableException
import sewlang.exception.ExpInvalidValueTypeException
import sewlang.exception.ExpIdentifierRequiredException
import org.scalatest.BeforeAndAfterAll

class ExpInterpreterTest extends AnyFunSuite with BeforeAndAfterAll with MockFactory with ExpTestUtilities {

  val testEnv = Environment(Map("null" -> NilV, "PI" -> NumberV(math.Pi), "zero" -> NumberV(0)))

  test("Exp Interpreting Basic Value Expressions") {
    assert(ExpInterpreter(parseSExprToExp("0"))(testEnv) == NumberV(0))
    assert(ExpInterpreter(parseSExprToExp("5.2"))(testEnv) == NumberV(5.2))
    assert(ExpInterpreter(parseSExprToExp("\"test\""))(testEnv) == StringV("test"))
    assert(ExpInterpreter(parseSExprToExp("\"\""))(testEnv) == StringV(""))
    assert(ExpInterpreter(parseSExprToExp("true"))(testEnv) == BoolV(true))
    assert(ExpInterpreter(parseSExprToExp("false"))(testEnv) == BoolV(false))
    assert(ExpInterpreter(parseSExprToExp("nil"))(testEnv) == NilV)
  }

  test("Exp Interpreting Bound Id Expressions") {
    assert(ExpInterpreter(parseSExprToExp("null"))(testEnv) == NilV)
    assert(ExpInterpreter(parseSExprToExp("PI"))(testEnv) == NumberV(math.Pi))
  }

  test("Exp Interpreting Unbound Id Expressions") {
    val caught =
      intercept[ExpUnboundVariableException] {
        ExpInterpreter(parseSExprToExp("num"))(testEnv)
      }
    assert(caught.getMessage.contains("num"))
  }

  test("Exp Interpreting Sum Expressions") {
    assert(ExpInterpreter(parseSExprToExp("(+ 2 3)"))(testEnv) == NumberV(5))
    assert(ExpInterpreter(parseSExprToExp("(+ 1.2 zero)"))(testEnv) == NumberV(1.2))
  }

  test("Exp Interpreting Multiplication Expressions") {
    assert(ExpInterpreter(parseSExprToExp("(* 2 3.5)"))(testEnv) == NumberV(7))
    assert(ExpInterpreter(parseSExprToExp("(* zero (+ 2 3))"))(testEnv) == NumberV(0))
  }

  test("Exp Interpreting Invalid Arithmetic Expressions") {
    var caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(+ 1 true)"))(testEnv)
      }
    assert(caught.getMessage.contains("arithmetic"))
    assert(caught.getMessage.contains("1"))
    assert(caught.getMessage.contains("true"))

    caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(* \"x\" 3)"))(testEnv)
      }
    assert(caught.getMessage.contains("arithmetic"))
    assert(caught.getMessage.contains("x"))
    assert(caught.getMessage.contains("3"))

    caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(* null false)"))(testEnv)
      }
    assert(caught.getMessage.contains("arithmetic"))
    assert(caught.getMessage.contains("nil"))
    assert(caught.getMessage.contains("false"))
  }

  test("Exp Interpreting Equal Expressions") {
    assert(ExpInterpreter(parseSExprToExp("(= zero 0)"))(testEnv) == BoolV(true))
    assert(ExpInterpreter(parseSExprToExp("(= 3 (+ 2 1))"))(testEnv) == BoolV(true))
    assert(ExpInterpreter(parseSExprToExp("(= 2 3)"))(testEnv) == BoolV(false))
  }

  test("Exp Interpreting Less Than Expressions") {
    assert(ExpInterpreter(parseSExprToExp("(< zero 1)"))(testEnv) == BoolV(true))
    assert(ExpInterpreter(parseSExprToExp("(< 3 (* 4 7))"))(testEnv) == BoolV(true))
    assert(ExpInterpreter(parseSExprToExp("(< (+ 2 3) 5)"))(testEnv) == BoolV(false))
  }

  test("Exp Interpreting Invalid Relational Expressions") {
    var caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(= 1 true)"))(testEnv)
      }
    assert(caught.getMessage.contains("relational"))
    assert(caught.getMessage.contains("1"))
    assert(caught.getMessage.contains("true"))

    caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(< false 3)"))(testEnv)
      }
    assert(caught.getMessage.contains("relational"))
    assert(caught.getMessage.contains("false"))
    assert(caught.getMessage.contains("3"))

    caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(= \"false\" false)"))(testEnv)
      }
    assert(caught.getMessage.contains("relational"))
    assert(caught.getMessage.contains("false"))
  }

  test("Exp Interpreting Not Expressions") {
    assert(ExpInterpreter(parseSExprToExp("(not true)"))(testEnv) == BoolV(false))
    assert(ExpInterpreter(parseSExprToExp("(not false)"))(testEnv) == BoolV(true))
    assert(ExpInterpreter(parseSExprToExp("(not (< zero 1))"))(testEnv) == BoolV(false))
    assert(ExpInterpreter(parseSExprToExp("(not (= 2 3))"))(testEnv) == BoolV(true))
  }

  test("Exp Interpreting And Expressions") {
    assert(ExpInterpreter(parseSExprToExp("(and true (not false))"))(testEnv) == BoolV(true))
    assert(ExpInterpreter(parseSExprToExp("(and true false)"))(testEnv) == BoolV(false))
    assert(ExpInterpreter(parseSExprToExp("(and false true)"))(testEnv) == BoolV(false))
    assert(ExpInterpreter(parseSExprToExp("(and false false)"))(testEnv) == BoolV(false))
    assert(ExpInterpreter(parseSExprToExp("(and (= (+ 2 3) 5) (< 0 1))"))(testEnv) == BoolV(true))
  }

  test("Exp Interpreting Invalid Logical Expressions") {
    var caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(not 1)"))(testEnv)
      }
    assert(caught.getMessage.contains("logical"))
    assert(caught.getMessage.contains("1"))

    caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(and false 3)"))(testEnv)
      }
    assert(caught.getMessage.contains("logical"))
    assert(caught.getMessage.contains("false"))
    assert(caught.getMessage.contains("3"))

    caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(and nil true)"))(testEnv)
      }
    assert(caught.getMessage.contains("logical"))
    assert(caught.getMessage.contains("nil"))
    assert(caught.getMessage.contains("true"))

    caught =
      intercept[ExpInvalidValueTypeException] {
        ExpInterpreter(parseSExprToExp("(and 1 1)"))(testEnv)
      }
    assert(caught.getMessage.contains("logical"))
    assert(caught.getMessage.contains("1"))
    assert(caught.getMessage.contains("1"))
  }

  test("Exp Interpreting Variable Declaration") {
    val localTestEnv = Environment(testEnv)
    assert(ExpInterpreter(parseSExprToExp("(var x 10)"))(localTestEnv) == NumberV(10))
    assert(ExpInterpreter(parseSExprToExp("(var continue true)"))(localTestEnv) == BoolV(true))
    assert(localTestEnv.lookup("x") == NumberV(10))
    assert(localTestEnv.lookup("continue") == BoolV(true))
  }

  test("Exp Interpreting Variable Assignment") {
    val localTestEnv = Environment(Map("x" -> NumberV(0), "continue" -> BoolV(true)), testEnv)
    assert(ExpInterpreter(parseSExprToExp("(set x (+ x 1))"))(localTestEnv) == NumberV(1))
    assert(ExpInterpreter(parseSExprToExp("(set continue false)"))(localTestEnv) == BoolV(false))
    assert(localTestEnv.lookup("x") == NumberV(1))
    assert(localTestEnv.lookup("continue") == BoolV(false))
  }

  test("Exp Interpreting Variable Declaration and Assignment Without Identifier") {
    val localTestEnv = Environment(testEnv)
    assertThrows[ExpIdentifierRequiredException] {
      ExpInterpreter(parseSExprToExp("(var 10 x)"))(localTestEnv)
    }

    assertThrows[ExpIdentifierRequiredException] {
      ExpInterpreter(parseSExprToExp("(set 10 10)"))(localTestEnv)
    }
  }

  test("Exp Interpreting If Expressions") {
    val localTestEnv = Environment(Map("continue" -> BoolV(true), "a" -> NumberV(1), "b" -> NumberV(2)))
    assert(ExpInterpreter(parseSExprToExp("(if (< a b) a b)"))(localTestEnv) == NumberV(1))
    assert(ExpInterpreter(parseSExprToExp("(if (= a b) b a)"))(localTestEnv) == NumberV(1))
    assert(ExpInterpreter(parseSExprToExp("(if (not (and (< a 2) (= b 2))) (+ a 1) (+ b 1))"))(localTestEnv) == NumberV(3))
  }

  test("Exp Interpreting If Expressions Without Boolean Condition") {
    assertThrows[ExpInvalidValueTypeException] {
      ExpInterpreter(parseSExprToExp("(if nil 1 2)"))(testEnv)
    }
  }

  test("Exp Interpreting While Expressions") {
    val localTestEnv = Environment(Map("continue" -> BoolV(true), "x" -> NumberV(0)))
    assert(ExpInterpreter(parseSExprToExp("(while false 1)"))(localTestEnv) == NilV)
    assert(ExpInterpreter(parseSExprToExp("(while continue (set continue false))"))(localTestEnv) == BoolV(false))
    assert(ExpInterpreter(parseSExprToExp("(while (< x 10) (set x (+ x 1)))"))(localTestEnv) == NumberV(10))
    assert(localTestEnv.lookup("x") == NumberV(10))
  }

  test("Exp Interpreting While Expressions Without Boolean Condition") {
    assertThrows[ExpInvalidValueTypeException] {
      ExpInterpreter(parseSExprToExp("(while 1 (+ 1 2))"))(testEnv)
    }
  }

  test("Exp Interpreting Block Expressions ") {
    val localTestEnv = Environment(Map("x" -> NumberV(10)))
    val input1 =
      """
        (begin
          (var x 0)
          (set x (+ x 1))
          x  
        )
      """
    assert(ExpInterpreter(parseSExprToExp(input1))(localTestEnv) == NumberV(1))
    assert(localTestEnv.lookup("x") == NumberV(10))

    val input2 =
      """
        (begin 
          (var y 1)
          (begin
            (var y true)
          )
          (set y (+ y 1))
        )
      """
    assert(ExpInterpreter(parseSExprToExp(input2))(localTestEnv) == NumberV(2))
    assertThrows[ExpUnboundVariableException](localTestEnv.lookup("y"))

    assert(ExpInterpreter(parseSExprToExp("(begin)"))(localTestEnv) == NilV)
  }

  test("Exp Interpreting Print Expressioon") {
    val printTest1 = mockFunction[Any, Unit]
    printTest1 expects ("x = 1")
    ExpInterpreter.print = printTest1
    assert(ExpInterpreter(parseSExprToExp("(print \"x = \" 1)"))(testEnv) == NilV)

    val printTest2 = mockFunction[Any, Unit]
    printTest2 expects ("truefalsenil")
    ExpInterpreter.print = printTest2
    assert(ExpInterpreter(parseSExprToExp("(print true false nil)"))(testEnv) == NilV)

    val printTest3 = mockFunction[Any, Unit]
    printTest3 expects ""
    ExpInterpreter.print = printTest3
    assert(ExpInterpreter(parseSExprToExp("(print)"))(testEnv) == NilV)

    val printTest4 = mockFunction[Any, Unit]
    printTest4 expects ("x = 2.5")
    ExpInterpreter.print = printTest4
    assert(ExpInterpreter(parseSExprToExp("(print \"x = \" 2.5)"))(testEnv) == NilV)
  }

  test("Exp Interpreting Read Number Expressioon") {
    val readTest1 = mockFunction[Double]
    readTest1 expects () returning 5.0
    ExpInterpreter.readDouble = readTest1
    assert(ExpInterpreter(parseSExprToExp("(read-num)"))(testEnv) == NumberV(5.0))

    val readTest2 = mockFunction[Double]
    readTest2 expects () returning 1
    readTest2 expects () returning 2
    ExpInterpreter.readDouble = readTest2
    assert(ExpInterpreter(parseSExprToExp("(+ (read-num) (read-num))"))(testEnv) == NumberV(3.0))
  }

  test("Exp Interpreting Increment and Sum Augmented Assignment Expressions") {
    val localTestEnv = Environment(Map("x" -> NumberV(0), "y" -> NumberV(2)))

    assert(ExpInterpreter(parseSExprToExp("(++ x)"))(localTestEnv) == NumberV(1))
    assert(localTestEnv.lookup("x") == NumberV(1))

    assert(ExpInterpreter(parseSExprToExp("(+ 3 (++ x))"))(localTestEnv) == NumberV(5))
    assert(localTestEnv.lookup("x") == NumberV(2))

    assert(ExpInterpreter(parseSExprToExp("(+= y 3)"))(localTestEnv) == NumberV(5))
    assert(localTestEnv.lookup("y") == NumberV(5))

    assert(ExpInterpreter(parseSExprToExp("(* 5 (+= y -3))"))(localTestEnv) == NumberV(10))
    assert(localTestEnv.lookup("y") == NumberV(2))
  }

  test("Exp Interpreting For Expressions") {
    val localTestEnv = Environment(testEnv)
    assert(ExpInterpreter(parseSExprToExp("(for (var x 0) (< x 10) (++ x) x)"))(localTestEnv) == NumberV(10))
    assert(ExpInterpreter(parseSExprToExp("(for (var x 10) (< 0 x) (set x (+ x -1)) x)"))(localTestEnv) == NumberV(0))
  }

  override def afterAll() = {
    ExpInterpreter.print = println
    ExpInterpreter.readDouble = io.StdIn.readDouble
  }

}