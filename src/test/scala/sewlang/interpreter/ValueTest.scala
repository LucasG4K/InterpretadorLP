package sewlang.interpreter

import sewlang.util.ExpTestUtilities
import org.scalatest.funsuite.AnyFunSuite
import sewlang.exception.ExpInvalidValueTypeException

class ValueTest extends AnyFunSuite {

  test("Value Number Type Verification") {
    val number: Value = NumberV(5.2)

    assert(number.isNumber)
    assert(!number.isBool)
    assert(!number.isString)
    assert(!number.isNil)

    assert(number.getNumber == 5.2)
    assertThrows[ExpInvalidValueTypeException](number.getBool)
    assertThrows[ExpInvalidValueTypeException](number.getString)
  }

  test("Value Boolean Type Verification") {
    val bool: Value = BoolV(true)

    assert(bool.isBool)
    assert(!bool.isNumber)
    assert(!bool.isString)
    assert(!bool.isNil)

    assert(bool.getBool == true)
    assertThrows[ExpInvalidValueTypeException](bool.getNumber)
    assertThrows[ExpInvalidValueTypeException](bool.getString)
  }

  test("Value String Type Verification") {
    val string: Value = StringV("test")

    assert(string.isString)
    assert(!string.isBool)
    assert(!string.isNumber)
    assert(!string.isNil)

    assert(string.getString == "test")
    assertThrows[ExpInvalidValueTypeException](string.getNumber)
    assertThrows[ExpInvalidValueTypeException](string.getBool)
  }

  test("Value Nil Type Verification") {
    val nil: Value = NilV

    assert(nil.isNil)
    assert(!nil.isString)
    assert(!nil.isBool)
    assert(!nil.isNumber)

    assertThrows[ExpInvalidValueTypeException](nil.getNumber)
    assertThrows[ExpInvalidValueTypeException](nil.getBool)
    assertThrows[ExpInvalidValueTypeException](nil.getString)
  }

}