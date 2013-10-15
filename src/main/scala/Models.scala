package ms.tobbetu.gdb4s;

import spray.json._
import DefaultJsonProtocol._

object Models {
	sealed trait GraphObject {
		def asJson : String
	}

	object GraphObject {
		def toEdge(source: String) = source.asJson.convertTo[Edge]
		def toNode(source: String) = source.asJson.convertTo[Node]
		def toRelationType(source: String) = source.asJson.convertTo[RelationType]
	}

	// spray-json implicits
	implicit val relationTypeProtocol = jsonFormat1(RelationType)
	implicit val nodeProtocol = jsonFormat1(Node)
	implicit val edgeProtocol = jsonFormat3(Edge)

	case class RelationType(name: Symbol) extends GraphObject {
		def asJson = this.toJson.compactPrint
	}
	case class Edge(from: Node, to: Node, relationtype: RelationType) extends GraphObject {
		def asJson = this.toJson.compactPrint
	}
	case class Node(id: Symbol) extends GraphObject {
		def asJson = this.toJson.compactPrint
	}


	implicit def str2node(value: Symbol) = Node(value)
	implicit def str2rel(value: Symbol) = RelationType(value)
	implicit def tup2edge(value: ((Symbol, Symbol), Symbol)) = {
		val ((from, rel), to) = value
		Edge(Node(from), Node(to), RelationType(rel))
	}
}