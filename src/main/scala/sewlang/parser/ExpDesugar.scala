package sewlang.parser

import sexpr.ast._

object ExpDesugar {

  def apply(sexp: SExpr) = desugar(sexp)

  def desugar(sexp: SExpr): SExpr = sexp match {

    case SList(List(SSym("++"), id))      => SList(List(SSym("set"), id, SList(List(SSym("+"), id, SNum(1))))) // (++ id) --> (set id (+ id 1))
    case SList(List(SSym("--"), id))      => SList(List(SSym("set"), id, SList(List(SSym("-"), id, SNum(1))))) // (-- id) --> (set id (- id 1))
    // #14 Implemente o desugar para a expressão (-- id) (FEITO)

    case SList(List(SSym("+="), id, exp)) => SList(List(SSym("set"), id, SList(List(SSym("+"), id, exp)))) // (+= id exp) --> (set id (+ id exp))
    case SList(List(SSym("-="), id, exp)) => SList(List(SSym("set"), id, SList(List(SSym("-"), id, exp)))) // (-= id exp) --> (set id (- id exp))
    case SList(List(SSym("*="), id, exp)) => SList(List(SSym("set"), id, SList(List(SSym("*"), id, exp)))) // (*= id exp) --> (set id (* id exp))
    case SList(List(SSym("/="), id, exp)) => SList(List(SSym("set"), id, SList(List(SSym("/"), id, exp)))) // (/= id exp) --> (set id (/ id exp))
    // #15 Implemente o desugar para as expressões (*= id exp), (-= id exp) e (/= id exp) (FEITO)

    case SList(List(SSym("for"), init, cond, mod, body)) => // (for init cond mod body) --> (begin init (while cond (begin body mod)))
      SList(List(
        SSym("begin"),
        init, // inicializa x -> 1
        SList(List(
          SSym("while"), cond, // 1 < 11
          SList(List(
            SSym("begin"),
            body, // print x
            mod)) // x++
    ))))

    case SList(List(SSym("repeat"), body, until_cond)) => // (repeat body until_cond) --> (begin body)(until_cond))
      SList(List(
        SSym("begin"),
        body,
        SList(List(
          SSym("while"), until_cond,
          SList(List(
            SSym("begin"),
            body))))))
    // #16 Implemente o desugar para a expressão (repeat body until-cond) (FEITO)

    case _ => sexp
  }

}