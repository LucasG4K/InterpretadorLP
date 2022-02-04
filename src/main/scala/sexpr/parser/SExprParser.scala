package sexpr.parser

import scala.util.parsing.combinator.RegexParsers

import sexpr.ast._
import sexpr.exception.SExprParserException

object SExprParser extends RegexParsers {

  def number: Parser[SNum] = ("""[-+]?[0-9]*\.?[0-9]+""".r) ^^ { (num: String) => SNum(num.toDouble) }

  def string: Parser[SString] = ("""\"[^\"]*\"""".r) ^^ { (str: String) => SString(str.substring(1, str.size - 1)) }

  def symbol: Parser[SSym] = ("""[\w\-+*=<>/]+""".r) ^^ { (str: String) => SSym(str) }

  def list: Parser[SList] = "(" ~> rep(sexpr) <~ ")" ^^ { exp => SList(exp) }

  def sexpr: Parser[SExpr] = number | string | symbol | list

  def sexprList: Parser[List[SExpr]] = rep(sexpr) ^^ { exp => exp }

  def parse(input: String): SExpr = super.parseAll(sexpr, input) match {
    case Success(sexpr, _) => sexpr
    case Failure(msg, _)   => throw SExprParserException(msg)
    case Error(msg, _)     => throw SExprParserException(msg)
  }

  def parseList(input: String): List[SExpr] = super.parseAll(sexprList, input) match {
    case Success(sexpr, _) => sexpr
    case Failure(msg, _)   => throw SExprParserException(msg)
    case Error(msg, _)     => throw SExprParserException(msg)
  }

  def apply(input: String) = this.parse(input)
}