package sewlang.interpreter

import scala.collection.mutable.Map

import sewlang.exception.ExpRedeclareVariableException
import sewlang.exception.ExpUnboundVariableException

class Environment(parent: Option[Environment]) {

  private val storage: Map[String, Value] = Map[String, Value]()

  def declare(id: String, value: Value): Value = {
    if (!storage.isDefinedAt(id)) {
      storage += id -> value
      value
    } else
      throw ExpRedeclareVariableException(s"variable '$id' is already declared locally")
  }

  def update(id: String, value: Value): Value = {
    resolve(id).storage.update(id, value)
    value
  }

  def lookup(id: String): Value = resolve(id).storage(id)

  def resolve(id: String): Environment = {
    if (storage.isDefinedAt(id))
      this
    else
      parent.getOrElse(throw ExpUnboundVariableException(s"unbound variable '$id'")).resolve(id)
  }
}

object Environment {
  def apply() = new Environment(None)
  def apply(parent: Environment) = new Environment(Some(parent))
  def apply(initialStorage: scala.collection.immutable.Map[String, Value], parent: Option[Environment] = None) = {
    val env = new Environment(parent)
    env.storage ++= initialStorage
    env
  }
  def apply(initialStorage: scala.collection.immutable.Map[String, Value], parent: Environment): Environment = apply(initialStorage, Some(parent))
}