package ms.tobbetu.gdb4s.backend

import scala.collection.mutable.{ Set => MSet }

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.EdgesetBackend._

/**
 * This backends implements very basic in-memory database
 */
package InMemoryBackend {

	class InMemoryStore() extends EdgesetBackend {

		val edges = MSet.empty[Edge]
		val db = new InMemoryDatabase

		class InMemoryDatabase() extends EdgesetDatabase {

			def edgeSet(predicate: PartialFunction[Edge, Boolean]): Set[Edge] =
				edges.filter(predicate).toSet

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

		}

	}

}