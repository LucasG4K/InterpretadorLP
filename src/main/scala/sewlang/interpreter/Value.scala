package sewlang.interpreter

import sewlang.exception._

sealed abstract class Value extends TypeVerification

case class NumberV(value: Double) extends Value with TypeVerification {
  override def isNumber: Boolean = true
  override def getNumber: Double = value
  override def toString = {
    val integerPart = value.toInt
    val decimalPart = value - integerPart
    if (decimalPart == 0)
      integerPart.toString
    else
      value.toString
  }
}

case class BoolV(value: Boolean) extends Value with TypeVerification {
  override def isBool: Boolean = true
  override def getBool: Boolean = value
  override def toString = value.toString
}

case class StringV(value: String) extends Value with TypeVerification {
  override def isString: Boolean = true
  override def getString: String = value
  override def toString = value.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r")
}

case object NilV extends Value with TypeVerification {
  override def isNil: Boolean = true
  override def toString = "nil"
}

trait TypeVerification {
  def isNumber: Boolean = false
  def isBool: Boolean = false
  def isString: Boolean = false
  def isNil: Boolean = false

  def getNumber: Double = throw ExpInvalidValueTypeException("value type is not a number")
  def getBool: Boolean = throw ExpInvalidValueTypeException("value type is not a boolean")
  def getString: String = throw ExpInvalidValueTypeException("value type is not a string")
}