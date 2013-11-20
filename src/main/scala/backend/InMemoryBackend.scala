package ms.tobbetu.gdb4s.backend

import scala.collection.mutable.{ Set => MSet }

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.EdgesetBackend._

/**
 * This backends implements very basic in-memory database
 */
package InMemoryBackend {

	trait InMemoryStore extends EdgesetBackend {

		val edges = MSet.empty[Edge]

		class InMemoryDatabase extends EdgesetDatabase {

			def edgeSet(predicate: PartialFunction[Edge, Boolean]): Set[Edge] =
				edges.filter(predicate).toSet

			/**
			 * Add Methods
			 */
			 def add(node: Node): Option[Node] = None
			 def add(edge: Edge): Option[Edge] = {
				try {
					edges.add(edge)
					Some(edge)
					} catch {
						case _ : Throwable => None
					}
			}


			 /**
			 * Remove Methods
			 */
			 def remove(node: Node): Set[Edge] = {
				try {
					val edgesToRemove = findAll(node)
					edges --= edgesToRemove
					edgesToRemove
					} catch {
						case _ : Throwable => Set.empty
					}
			}
			 def remove(edge: Edge): Option[Edge] = {
				try {
					edges.remove(edge)
					Some(edge)
					} catch {
						case _ : Throwable => None
					}
			}

		}

	}

}