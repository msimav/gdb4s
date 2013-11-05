package ms.tobbetu.gdb4s

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

import ms.tobbetu.gdb4s.api.ApiServiceActor
import ms.tobbetu.gdb4s.core.DatabaseWorker._


object Main extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("gdb4s")

  val path = java.io.File.createTempFile("gdb4s","test") // new File("/home/mustafa/workspace/scala/gdb4s/database")
  val backend = system.actorOf(Props(classOf[FileSystemDatabaseWorkerActor], path), "backend")

  // create and start our service actor
  val service = system.actorOf(Props(classOf[ApiServiceActor], backend), "rest-api")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}