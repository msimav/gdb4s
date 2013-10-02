package ms.tobbetu.gdb4s;

object Models {
	sealed trait GraphObject

	case class RelationType(name: String) extends GraphObject
	case class Edge(in: Node, out: Node, relationtype: RelationType) extends GraphObject
	case class Node(id: String) extends GraphObject

	implicit def str2node(value: String) = Node(value)
	implicit def str2rel(value: String) = RelationType(value)
	implicit def tup2edge(value: ((String, String), String)) = {
		val ((in, rel), out) = value
		Edge(Node(in), Node(out), RelationType(rel))
	}
}