name := "sewlang-interpreter"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalamock" %% "scalamock" % "5.1.0" % Test,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1"
)
