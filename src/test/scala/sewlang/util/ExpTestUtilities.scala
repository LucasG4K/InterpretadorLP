package sewlang.util

import sexpr.parser.SExprParser
import sewlang.parser.ExpParser

trait ExpTestUtilities {
  def parseSExpr(sexpr: String) = SExprParser(sexpr)
  def parseSExprToExp(sexpr: String) = ExpParser(SExprParser.parse(sexpr))
}