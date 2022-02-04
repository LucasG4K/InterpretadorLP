import sewlang.runner.ExpFileRunner

object Main extends App {
  if (args.length == 0) {
    Console.err.println("sewlang parsing error: it is necessary to pass a file")
  } else {
    val filePath = args(0)
    ExpFileRunner.run(filePath)
  }
}