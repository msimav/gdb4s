package ms.tobbetu.gdb4s.backend

import ms.tobbetu.gdb4s.Models._

package Backend {

	/**
	 * DatabaseBackend trait defines a cake pattern to store and query data
	 * This operations include search for objects,
	 * relations or outgoing relations from given objects,
	 * add or remove GraphObjects
	 */
	trait DatabaseBackend {
		val db: Database
		trait Database {

			/**
			 * Query Methods
			 */
			def findOutgoing(from: Node): Set[Edge]
			def findIngoing(to: Node): Set[Edge]
			def findOutgoing(from: Node, relationtype: RelationType): Set[Edge]
			def findIngoing(to: Node, relationtype: RelationType): Set[Edge]
			def findBetween(from: Node, to: Node): Set[Edge]
			def findAll(node: Node): Set[Edge]
			def findAll(relationtype: RelationType): Set[Edge]
			def exists(edge: Edge): Option[Edge]

			// OMG WTF!!
			def getLabels(str: String): Set[Edge]

			/**
			 * Add Methods
			 */
			 def add(node: Node): Option[Node]
			 def add(edge: Edge): Option[Edge]

			 /**
			 * Remove Methods
			 */
			 def remove(node: Node): Set[Edge]
			 def remove(edge: Edge): Option[Edge]

			 /**
			 * Update Methods
			 */
			 def update(oldNode: Node, newNode: Node): Set[Edge]
			 def update(oldEdge: Edge, newEdge: Edge): Option[Edge]
		}
	}

}