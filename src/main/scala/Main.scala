package ms.tobbetu.gdb4s

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

import ms.tobbetu.gdb4s.api.ApiServiceActor
import ms.tobbetu.gdb4s.core.DatabaseWorker._

import ms.tobbetu.gdb4s.backend.Backend._
import ms.tobbetu.gdb4s.backend.InMemoryBackend._
import ms.tobbetu.gdb4s.backend.FilesystemBackend._

object Main extends App {
  class InMemoryDatabaseActor extends DatabaseWorkerActor
  with InMemoryStore {
      val db = new InMemoryDatabase
    }
  class FileSystemDatabaseActor extends DatabaseWorkerActor
  with FilesystemStore {
      val path = new File("/home/mustafa/.gdb4s/database")
      val db = new FilesystemDatabase(path)
    }

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("gdb4s")

  val backend = system.actorOf(Props[InMemoryDatabaseActor], "backend")

  // create and start our service actor
  val service = system.actorOf(Props(classOf[ApiServiceActor], backend), "rest-api")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}