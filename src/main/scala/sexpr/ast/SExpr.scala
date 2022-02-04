package sexpr.ast

sealed abstract class SExpr
case class SNum(num: Double) extends SExpr {
  override def toString = num.toString
}
case class SString(string: String) extends SExpr {
  override def toString = string
}
case class SSym(symbol: String) extends SExpr {
  override def toString = symbol
}
case class SList(list: List[SExpr]) extends SExpr {
  override def toString = s"(${list.mkString(" ")})"
}