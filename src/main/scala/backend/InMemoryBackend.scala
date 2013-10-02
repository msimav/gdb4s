package ms.tobbetu.gdb4s.backend

import scala.collection.mutable.{ Set => MSet }

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.Backend._

/**
 * This backends implements very basic in-memory database
 */
package InMemoryBackend {

	class InMemory() extends QuerySearch {

		val nodes = MSet.empty[Node]
		val edges = MSet.empty[Edge]

		val finder = new InMemoryFinder

		class InMemoryFinder() extends Finder {
			def find(node: Node): Option[Node] = nodes.find { _ == node }

			def find(in: Node, relationtype: RelationType): Set[Edge] =
				edges.filter {
					case Edge(ein, _, rel) => in == ein && rel == relationtype
				} toSet

			def find(relationtype: RelationType): Set[Edge] =
				edges.filter {
					case Edge(_, _, rel) => rel == relationtype
				} toSet
		}

	}

}