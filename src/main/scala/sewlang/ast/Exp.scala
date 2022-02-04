package sewlang.ast

sealed abstract class Exp

// Value Expressions
case class NumberExp(value: Double) extends Exp
case object TrueExp extends Exp
case object FalseExp extends Exp
case object NilExp extends Exp
case class StringExp(value: String) extends Exp
case class IdExp(id: String) extends Exp

// Arithmetic Expressions
case class SumExp(exp1: Exp, exp2: Exp) extends Exp // (+ exp1 exp2)
case class MultExp(exp1: Exp, exp2: Exp) extends Exp // (* exp1 exp2)
case class SubExp(exp1: Exp, exp2: Exp) extends Exp // (- exp1 exp2)
case class DivExp(exp1: Exp, exp2: Exp) extends Exp // (/ exp1 exp2)
case class InvExp(exp: Exp) extends Exp // (- exp)
// #1 Defina a AST para as expressões (- exp1 exp2), (/ exp1 exp2) e (- exp)

// Relation Expressions
case class EqualExp(exp1: Exp, exp2: Exp) extends Exp // (= exp1 exp2)
case class LessThanExp(exp1: Exp, exp2: Exp) extends Exp // (< exp1 exp2)
case class GreaterThanExp(exp1: Exp, exp2: Exp) extends Exp // (> exp1 exp2)
case class LessOrEqualExp(exp1: Exp, exp2: Exp) extends Exp // (<= exp1 exp2)
case class GreaterOrEqualExp(exp1: Exp, exp2: Exp) extends Exp // (>= exp1 exp2)
// #2 Defina a AST para as expressões (<= exp1 exp2), (> exp1 exp2) e (>= exp1 exp2)

// Boolean Expressions
case class NotExp(exp: Exp) extends Exp // (not exp)
case class AndExp(exp1: Exp, exp2: Exp) extends Exp // (and exp1 exp2)
case class OrExp(exp1: Exp, exp2: Exp) extends Exp // (or exp1 exp2)
// #3 Defina a AST para a expressão (or exp1 exp2)

// Variable Declaration and Assignment Expressions
case class VarDeclExp(id: Exp, exp: Exp) extends Exp // (var id exp)
case class VarAssignExp(id: Exp, exp: Exp) extends Exp // (set id exp)

// If Expression
case class IfExp(cond: Exp, thenExp: Exp, elseExp: Exp) extends Exp // (if cond exp-then exp-else)

// Loop Expression
case class WhileExp(cond: Exp, doExp: Exp) extends Exp // (while cond do-exp)

// Block Expression
case class BlockExp(exps: List[Exp]) extends Exp // (begin exp*)

// IO Expressions
case class PrintExp(exps: List[Exp]) extends Exp // (print exp*)
case object ReadNumExp extends Exp // (read-num)
// #4 Defina a AST para as expressões (read-bool) e (read-str)