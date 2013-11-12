package ms.tobbetu.gdb4s.core

import java.io.File

import Messages._
import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.Backend._
import ms.tobbetu.gdb4s.backend.InMemoryBackend._
import ms.tobbetu.gdb4s.backend.FilesystemBackend._
import akka.actor.{ ActorRef, Props, Actor }

package object DatabaseWorker {

  trait DatabaseWorkerActor extends Actor { this: DatabaseBackend =>

    def receive = {
      case QueryAll(node) => sender ! db.findAll(node)
      case Query(Some(from), None, None) => sender ! db.findOutgoing(from)
      case Query(None, Some(to), None) => sender ! db.findIngoing(to)
      case Query(None, None, Some(rel)) => sender ! db.findAll(rel)
      case Query(Some(from), None, Some(rel)) => sender ! db.findOutgoing(from, rel)
      case Query(None, Some(to), Some(rel)) => sender ! db.findIngoing(to, rel)
      case Query(Some(from), Some(to), None) => sender ! db.findBetween(from, to)
      case Query(Some(from), Some(to), Some(rel)) => sender ! db.exists(Edge(from, to, rel))

      case Add(Left(node)) => sender ! db.add(node)
      case Add(Right(edge)) => sender ! db.add(edge)
      case Remove(Left(node)) => sender ! db.remove(node)
      case Remove(Right(edge)) => sender ! db.remove(edge)
      case Update(Left(node1), Left(node2)) => sender ! db.update(node1, node2)
      case Update(Right(edge1), Right(edge2)) => sender ! db.update(edge1, edge2)
    }

  }

  class FileSystemDatabaseActor(path: File) extends FilesystemStore(path)
    with DatabaseWorkerActor

  class InMemoryDatabaseActor extends InMemoryStore
    with DatabaseWorkerActor

  def fsProps(path: File) = Props(classOf[FileSystemDatabaseActor], path)

  val memProps = Props[InMemoryDatabaseActor]
}