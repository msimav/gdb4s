package ms.tobbetu.gdb4s;

object Models {
	sealed trait GraphObject

	case class RelationType(name: Symbol) extends GraphObject
	case class Edge(from: Node, to: Node, relationtype: RelationType) extends GraphObject
	case class Node(id: Symbol) extends GraphObject

	implicit def str2node(value: Symbol) = Node(value)
	implicit def str2rel(value: Symbol) = RelationType(value)
	implicit def tup2edge(value: ((Symbol, Symbol), Symbol)) = {
		val ((from, rel), to) = value
		Edge(Node(from), Node(to), RelationType(rel))
	}
}