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

			/**
			 * Add Methods
			 */
			 def add(node: Node): Unit
			 def add(edge: Edge): Unit

			 /**
			 * Remove Methods
			 */
			 def remove(node: Node): Unit
			 def remove(edge: Edge): Unit

			 /**
			 * Update Methods
			 */
			 def update(oldNode: Node, newNode: Node): Unit
			 def update(oldEdge: Edge, newEdge: Edge): Unit
		}
	}

}