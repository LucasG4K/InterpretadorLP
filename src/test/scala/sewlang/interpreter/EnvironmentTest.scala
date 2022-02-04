package sewlang.interpreter

import org.scalatest.funsuite.AnyFunSuite
import sewlang.exception.ExpRedeclareVariableException
import sewlang.exception.ExpUnboundVariableException

class EnvironmentTest extends AnyFunSuite {

  test("Environment Declare and Lookup Local Variable") {
    val localEnv = Environment()
    localEnv.declare("num", NumberV(5))
    localEnv.declare("str", StringV("test"))
    assert(localEnv.lookup("num") == NumberV(5))
    assert(localEnv.lookup("str") == StringV("test"))
  }

  test("Environment Redeclare Already Declared Local Variable") {
    val localEnv = Environment()
    localEnv.declare("num", NumberV(5))
    val caught =
      intercept[ExpRedeclareVariableException] {
        localEnv.declare("num", NumberV(5))
      }
    assert(caught.getMessage.contains("num"))
  }

  test("Environment Update Local Variable") {
    val localEnv = Environment()
    localEnv.declare("continue", BoolV(true))
    assert(localEnv.lookup("continue") == BoolV(true))
    localEnv.update("continue", BoolV(false))
    assert(localEnv.lookup("continue") == BoolV(false))
  }

  test("Environment Update Unboud Local Variable") {
    val localEnv = Environment()
    val caught =
      intercept[ExpUnboundVariableException] {
        localEnv.update("num", NumberV(5))
      }
    assert(caught.getMessage.contains("num"))
  }

  test("Environment Lookup Unboud Local Variable") {
    val localEnv = Environment()
    val caught =
      intercept[ExpUnboundVariableException] {
        localEnv.lookup("num")
      }
    assert(caught.getMessage.contains("num"))
  }

  test("Environment Declare and Lookup Local Variable with Same Name of Variable in Parent") {
    val parentEnv = Environment(Map("num" -> NumberV(5), "id" -> BoolV(true)))
    val localEnv = Environment(parentEnv)
    localEnv.declare("num", NumberV(2))
    localEnv.declare("id", StringV("other value type"))
    assert(localEnv.lookup("num") == NumberV(2))
    assert(localEnv.lookup("id") == StringV("other value type"))
    assert(parentEnv.lookup("num") == NumberV(5)) // parent environment remains the same
    assert(parentEnv.lookup("id") == BoolV(true))
  }

  test("Environment Lookup Variable in Parent") {
    val parentEnv = Environment(Map("num" -> NumberV(72.23), "null" -> NilV))
    val localEnv = Environment(parentEnv)
    assert(localEnv.lookup("num") == NumberV(72.23))
    assert(localEnv.lookup("null") == NilV)
  }

  test("Environment Update Variable in Parent") {
    val parentEnv = Environment(Map("num" -> NumberV(72.23)))
    val localEnv = Environment(parentEnv)

    assert(localEnv.lookup("num") == NumberV(72.23))
    assert(parentEnv.lookup("num") == NumberV(72.23))

    localEnv.update("num", NumberV(5))

    assert(localEnv.lookup("num") == NumberV(5))
    assert(parentEnv.lookup("num") == NumberV(5))
  }

  test("Environment Lookup Unboud Variable with Parent") {
    val parentEnv = Environment(Map("null" -> NilV))
    val localEnv = Environment(parentEnv)
    val caught =
      intercept[ExpUnboundVariableException] {
        localEnv.lookup("num")
      }
    assert(caught.getMessage.contains("num"))
  }

  test("Environment Update Unboud Variable with Parent") {
    val parentEnv = Environment(Map("null" -> NilV))
    val localEnv = Environment(parentEnv)
    val caught =
      intercept[ExpUnboundVariableException] {
        localEnv.update("num", NumberV(5))
      }
    assert(caught.getMessage.contains("num"))
  }

}