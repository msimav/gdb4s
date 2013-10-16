package ms.tobbetu.gdb4s.backend

import scala.collection.mutable.{ Set => MSet }

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.Backend._

/**
 * This backends implements very basic in-memory database
 */
package InMemoryBackend {

	class InMemoryStore() extends DatabaseBackend {

		val edges = MSet.empty[Edge]
		val db = new InMemoryDatabase

		class InMemoryDatabase() extends Database {

			/**
			 * Query Methods
			 */
			def findOutgoing(from: Node): Set[Edge] =
				edges.filter {
					case Edge(from2, _, _) => from == from2
				} toSet

			def findIngoing(to: Node): Set[Edge] =
				edges.filter {
					case Edge(_, to2, _) => to == to2
				} toSet

			def findOutgoing(from: Node, relationtype: RelationType): Set[Edge] =
				edges.filter {
					case Edge(from2, _, rel) => from == from2 && rel == relationtype
				} toSet

			def findIngoing(to: Node, relationtype: RelationType): Set[Edge] =
				edges.filter {
					case Edge(_, to2, rel) => to == to2 && rel == relationtype
				} toSet

			def findAll(node: Node): Set[Edge] =
				edges.filter {
					case Edge(from, to, _) => node == from || node == to
				} toSet // or findIngoing & findOutgoing

			def findAll(relationtype: RelationType): Set[Edge] =
				edges.filter {
					case Edge(_, _, rel) => rel == relationtype
				} toSet


			/**
			 * Add Methods
			 */
			 def add(node: Node): Unit = () // Do nothing
			 def add(edge: Edge): Unit = edges.add(edge)


			 /**
			 * Remove Methods
			 */
			 def remove(node: Node): Unit =  edges --= findAll(node)
			 def remove(edge: Edge): Unit = edges.remove(edge)


			 /**
			 * Update Methods
			 */
			 def update(oldNode: Node, newNode: Node): Unit =
				for(e @ Edge(to, from, rel) <- findAll(oldNode)) {
						val newTo = if (to == oldNode) newNode else to
						val newFrom = if (from == oldNode) newNode else from

						remove(e)
						add(Edge(newTo, newFrom, rel))
				}

			 def update(oldEdge: Edge, newEdge: Edge): Unit = {
				remove(oldEdge)
				add(newEdge)
			 }
		}

	}

}