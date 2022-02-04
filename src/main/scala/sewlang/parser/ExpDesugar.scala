package sewlang.parser

import sexpr.ast._

object ExpDesugar {

  def apply(sexp: SExpr) = desugar(sexp)

  def desugar(sexp: SExpr): SExpr = sexp match {

    case SList(List(SSym("++"), id))      => SList(List(SSym("set"), id, SList(List(SSym("+"), id, SNum(1))))) // (++ id) --> (set id (+ id 1))
    // #14 Implemente o desugar para a expressão (-- id)

    case SList(List(SSym("+="), id, exp)) => SList(List(SSym("set"), id, SList(List(SSym("+"), id, exp)))) // (+= id exp) --> (set id (+ id exp))
    // #15 Implemente o desugar para as expressões (*= id exp), (-= id exp) e (/= id exp)

    case SList(List(SSym("for"), init, cond, mod, body)) => // (for init cond mod body) --> (begin init (while cond (begin body mod)))
      SList(List(
        SSym("begin"),
        init,
        SList(List(
          SSym("while"), cond,
          SList(List(
            SSym("begin"),
            body,
            mod))))))

    // #16 Implemente o desugar para a expressão (repeat body until-cond)

    case _ => sexp
  }

}