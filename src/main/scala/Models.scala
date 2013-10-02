package ms.tobbetu.gdb4s;

object Models {
	sealed trait GraphObject

	case class RelationType(name: String) extends GraphObject
	case class Edge(in: Node, out: Node, relationtype: RelationType) extends GraphObject
	case class Node(id: String) extends GraphObject
}