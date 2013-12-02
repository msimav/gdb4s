package ms.tobbetu.gdb4s.core

import akka.actor.ActorRef
import ms.tobbetu.gdb4s.Models._

package object Messages {
  sealed trait DatabaseMessage
  case class Query(from: Option[Node], to: Option[Node], rel: Option[RelationType]) extends DatabaseMessage
  case class QueryAll(node: Node) extends DatabaseMessage
  case class Add(obj: Either[Node, Edge]) extends DatabaseMessage
  case class Remove(obj: Either[Node, Edge]) extends DatabaseMessage
  case class Update(obj1: Either[Node, Edge], obj2: Either[Node, Edge]) extends DatabaseMessage

  sealed trait ProtocolMessage
  case class WorkDone(worker: ActorRef, work: DatabaseMessage) extends ProtocolMessage
}
