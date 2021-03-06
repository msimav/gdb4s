package ms.tobbetu.gdb4s

import java.io.File

import akka.actor.{ActorSystem, Props, ActorRef, Actor}
import akka.io.IO
import spray.can.Http

import akka.util.Timeout
import scala.concurrent.duration._

import ms.tobbetu.gdb4s.api.{ ApiService, NamespaceService }
import ms.tobbetu.gdb4s.core.DatabaseWorker._

import ms.tobbetu.gdb4s.backend.Backend._
import ms.tobbetu.gdb4s.backend.InMemoryBackend._
import ms.tobbetu.gdb4s.backend.FilesystemBackend._

object Main {
  class InMemoryDatabaseActor extends DatabaseWorkerActor
  with InMemoryStore with NamespacedBackend {
      val db = new InMemoryDatabase with NamespacedDatabase
    }
  class FileSystemDatabaseActor extends DatabaseWorkerActor
  with FilesystemStore {
      val path = new File("/home/mustafa/.gdb4s/database")
      val db = new FilesystemDatabase(path)
    }

  class ApiServiceActor(val backend: ActorRef) extends Actor with ApiService with NamespaceService {
    def actorRefFactory = context
    implicit val timeout = Timeout(10, SECONDS)

    def receive = runRoute {
      pathPrefix("db") { dbRoute } ~
      pathPrefix("ns") { nsRoute } ~
      pathPrefix("batch") { batchRoute } ~
      frontend
    }
  }

  def main(args: Array[String]): Unit = args match {
    case Array(port, path) =>

        // we need an ActorSystem to host our application in
        implicit val system = ActorSystem("gdb4s")

        val backend = system.actorOf(Props[InMemoryDatabaseActor], "backend")

        // create and start our service actor
        val service = system.actorOf(Props(classOf[ApiServiceActor], backend), "rest-api")

        // start a new HTTP server on port 8080 with our service actor as the handler
        IO(Http) ! Http.Bind(service, interface = "localhost", port = port)
  }

}