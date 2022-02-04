package sewlang.interpreter

import sewlang.ast._
import sewlang.exception.ExpInvalidValueTypeException
import sewlang.exception.ExpIdentifierRequiredException

object ExpInterpreter {

  val globalEnvironment = Environment(Map(
    "null" -> NilV,
    "PI" -> NumberV(math.Pi),
    "E" -> NumberV(math.E)))

  def apply(exp: Exp)(env: Environment): Value = eval(exp)(env)

  def eval(exp: Exp)(env: Environment): Value = exp match {
    case NumberExp(n)                  => NumberV(n)
    case StringExp(s)                  => StringV(s)
    case TrueExp                       => BoolV(true)
    case FalseExp                      => BoolV(false)
    case NilExp                        => NilV

    case IdExp(id)                     => env.lookup(id)

    case VarDeclExp(id, exp)           => evalVarDecl(id, exp)(env)
    case VarAssignExp(id, exp)         => evalVarAssign(id, exp)(env)

    case SumExp(exp1, exp2)            => evalArithExp(_ + _, eval(exp1)(env), eval(exp2)(env))
    case MultExp(exp1, exp2)           => evalArithExp(_ * _, eval(exp1)(env), eval(exp2)(env))
    case SubExp(exp1, exp2)            => evalArithExp(_ - _, eval(exp1)(env), eval(exp2)(env))
    case DivExp(exp1, exp2)            => evalArithExp(_ / _, eval(exp1)(env), eval(exp2)(env))
    case InvExp(exp)                   => evalArithInvExp(_ - _, eval(exp)(env))
    // #10 Implemente a avaliação das expressões (- exp1 exp2), (/ exp1 exp2) e (- exp)

    case EqualExp(exp1, exp2)          => evalRelationalExp(_ == _, eval(exp1)(env), eval(exp2)(env))
    case LessThanExp(exp1, exp2)       => evalRelationalExp(_ < _, eval(exp1)(env), eval(exp2)(env))
    case GreaterThanExp(exp1, exp2)    => evalRelationalExp(_ > _, eval(exp1)(env), eval(exp2)(env))
    case LessOrEqualExp(exp1, exp2)    => evalRelationalExp(_ <= _, eval(exp1)(env), eval(exp2)(env))
    case GreaterOrEqualExp(exp1, exp2) => evalRelationalExp(_ >= _, eval(exp1)(env), eval(exp2)(env))
    // #11 Implemente a avaliação das expressões (<= exp1 exp2), (> exp1 exp2) e (>= exp1 exp2)

    case NotExp(exp)                   => evalNotExp(eval(exp)(env))
    case AndExp(exp1, exp2)            => evalBoolExp(_ && _, eval(exp1)(env), eval(exp2)(env))
    case OrExp(exp1, exp2)             => evalBoolExp(_ || _, eval(exp1)(env), eval(exp2)(env))
    // #12 Implemente a avaliação da expressão (or exp1 exp2)

    case IfExp(cond, thenExp, elseExp) => evalIfExp(eval(cond)(env), thenExp, elseExp)(env)

    case WhileExp(cond, doExp)         => evalWhileExp(cond, doExp)(env)

    case BlockExp(exps)                => evalBlockExp(exps)(env)

    case PrintExp(exps)                => evalPrintExp(exps)(env)

    case ReadNumExp                    => NumberV(readDouble())
    // #13 Implemente a avaliação das expressões (read-bool) e (read-str)
  }

  private[interpreter] var print: (Any) => Unit = println // for testing purposes
  private[interpreter] var readDouble: () => Double = io.StdIn.readDouble // for testing purposes

  private def evalVarDecl(id: Exp, exp: Exp)(env: Environment): Value = id match {
    case IdExp(id) => env.declare(id, eval(exp)(env))
    case _         => throw ExpIdentifierRequiredException("identifier is required in a variable declaration")
  }

  private def evalVarAssign(id: Exp, exp: Exp)(env: Environment): Value = id match {
    case IdExp(id) => env.update(id, eval(exp)(env))
    case _         => throw ExpIdentifierRequiredException("identifier is required in a variable assignment")
  }

  private def evalArithExp(op: (Double, Double) => Double, val1: Value, val2: Value): Value = (val1, val2) match {
    case (NumberV(num1), NumberV(num2)) => NumberV(op(num1, num2))
    case _                              => throw ExpInvalidValueTypeException(s"arithmetic expression requires number values but received '$val1' and '$val2'")
  }

  private def evalArithInvExp(op: (Double, Double) => Double, val1: Value): Value = (val1) match {
    case (NumberV(num)) => NumberV(op(0, num))
    case _                              => throw ExpInvalidValueTypeException(s"arithmetic expression requires number values but received '$val1'")
  }

  private def evalRelationalExp(op: (Double, Double) => Boolean, val1: Value, val2: Value): Value = (val1, val2) match {
    case (NumberV(num1), NumberV(num2)) => BoolV(op(num1, num2))
    case _                              => throw ExpInvalidValueTypeException(s"relational expression requires number values but received '$val1' and '$val2'")
  }

  private def evalNotExp(value: Value): Value = value match {
    case BoolV(bool) => BoolV(!bool)
    case _           => throw ExpInvalidValueTypeException(s"logical expression requires boolean values but received '$value'")
  }

  private def evalBoolExp(op: (Boolean, Boolean) => Boolean, val1: Value, val2: Value): Value = (val1, val2) match {
    case (BoolV(bool1), BoolV(bool2)) => BoolV(op(bool1, bool2))
    case _                            => throw ExpInvalidValueTypeException(s"logical expression requires boolean values but received '$val1' and '$val2'")
  }

  private def evalIfExp(cond: Value, thenExp: Exp, elseExp: Exp)(env: Environment): Value = cond match {
    case BoolV(true)  => eval(thenExp)(env)
    case BoolV(false) => eval(elseExp)(env)
    case _            => throw ExpInvalidValueTypeException(s"if expression requires a boolean expression in the condition")
  }

  private def evalWhileExp(cond: Exp, doExp: Exp)(env: Environment): Value = {
    @annotation.tailrec
    def loopWhile(cond: Exp, doExp: Exp, resultVal: Value): Value = eval(cond)(env) match {
      case BoolV(false) => resultVal
      case BoolV(true)  => loopWhile(cond, doExp, eval(doExp)(env))
      case _            => throw ExpInvalidValueTypeException(s"while expression requires a boolean expression in the condition")
    }
    loopWhile(cond, doExp, NilV)
  }

  private def evalBlockExp(exps: List[Exp])(env: Environment): Value = {
    val blockEnv = Environment(env)
    val defaultVal: Value = NilV
    exps.foldLeft(defaultVal)((value: Value, exp: Exp) => eval(exp)(blockEnv))
  } 

  private def evalPrintExp(exps: List[Exp])(env: Environment): Value = {
    print(exps.map(exp => eval(exp)(env)).mkString(""))
    NilV
  }
  
}