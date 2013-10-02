package ms.tobbetu.gdb4s.backend

import ms.tobbetu.gdb4s.Models._

package Backend {

	/**
	 * QuerySearch trait defines a cake pattern to search for objects,
	 * relations or outgoing relations from given objects
	 */
	trait QuerySearch {
		val finder: Finder
		trait Finder {
			def find(node: Node): Option[Node]
			def find(in: Node, relationtype: RelationType): Set[Edge]
			def find(relationtype: RelationType): Set[Edge]
		}
	}

}