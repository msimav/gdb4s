package ms.tobbetu.gdb4s;

import scala.language.implicitConversions
import spray.json._

object Models {
	sealed trait GraphObject

	case class RelationType(name: String) extends GraphObject
	case class Edge(from: Node, to: Node, relationtype: RelationType) extends GraphObject
	case class Node(id: String) extends GraphObject

	// spray-json protocol
	object GraphJsonProtocol extends DefaultJsonProtocol {
		implicit val nodeFormat = jsonFormat1(Node)
		implicit object TripleJsonFormat extends RootJsonFormat[Edge] {

			def write(e: Edge) = JsObject(
	      "from" -> JsString(e.from.id),
	      "to" -> JsString(e.to.id),
	      "relationtype" -> JsString(e.relationtype.name))

			def read(value: JsValue) = {
	      value.asJsObject.getFields("from", "to", "relationtype") match {
	        case Seq(JsString(from), JsString(to), JsString(rel)) =>
	          Edge(Node(from), Node(to), RelationType(rel))
	        case _ => throw new DeserializationException("Edge expected")
	      }
	    }
		}
	}

	implicit def sym2node(value: Symbol) = Node(value.name)
	implicit def str2node(value: String) = Node(value)
	implicit def sym2rel(value: Symbol) = RelationType(value.name)
	implicit def sym2edge(value: ((Symbol, Symbol), Symbol)) = {
		val ((from, rel), to) = value
		Edge(Node(from.name), Node(to.name), RelationType(rel.name))
	}
	implicit def str2edge(value: ((String, String), String)) = {
		val ((from, rel), to) = value
		Edge(Node(from), Node(to), RelationType(rel))
	}
}