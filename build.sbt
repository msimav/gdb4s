name := "gdb4s"

version := "0.0.1"

scalaVersion := "2.10.0"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.2.3",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.3",
    "io.spray" %%  "spray-json" % "1.2.5",
    "io.spray" %  "spray-can"     % "1.2-RC2",
    "io.spray" %  "spray-routing" % "1.2-RC2",
    "io.spray" %  "spray-testkit" % "1.2-RC2",
    "org.scalatest" % "scalatest_2.10" % "2.0.RC1" % "test"
)
