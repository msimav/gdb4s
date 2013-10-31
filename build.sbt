name := "gdb4s"

version := "0.0.1"

scalaVersion := "2.10.0"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= Seq(
    "io.spray" %%  "spray-json" % "1.2.5",
    "org.scalatest" % "scalatest_2.10" % "2.0.RC1" % "test"
)
