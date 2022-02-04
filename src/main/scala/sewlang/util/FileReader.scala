package sewlang.util

import scala.util.Try

object FileReader {

  def sourceCode(filePath: String): Try[String] = Try {
    io.Source.fromFile(filePath).getLines().mkString("\n")
  }

}