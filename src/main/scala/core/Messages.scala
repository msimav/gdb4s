package ms.tobbetu.gdb4s.core

import ms.tobbetu.gdb4s.Models._

package object Messages {
  sealed trait ProtocolMessage
  case class Query(from: Option[Node], to: Option[Node], rel: Option[RelationType]) extends ProtocolMessage
  case class QueryAll(node: Node) extends ProtocolMessage
  case class Add(obj: Either[Node, Edge]) extends ProtocolMessage
  case class Remove(obj: Either[Node, Edge]) extends ProtocolMessage
  case class Update(obj1: Either[Node, Edge], obj2: Either[Node, Edge]) extends ProtocolMessage
  case class Label(str: String) extends ProtocolMessage
}
