package sewlang.exception

case class ExpParserException(msg: String) extends Exception(msg)
case class ExpInvalidValueTypeException(msg: String) extends Exception(msg)
case class ExpUnboundVariableException(msg: String) extends Exception(msg)
case class ExpRedeclareVariableException(msg: String) extends Exception(msg)
case class ExpIdentifierRequiredException(msg: String) extends Exception(msg)
case class ExpIlegalStartOfSimplePatternException(msg: String) extends Exception(msg)