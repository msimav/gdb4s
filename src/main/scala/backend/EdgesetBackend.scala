package ms.tobbetu.gdb4s.backend

import scala.language.postfixOps

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.Backend._

/**
 * This backends implements abstract database as EdgeSet
 */
package EdgesetBackend {

  trait EdgesetBackend extends DatabaseBackend {

    val db: EdgesetDatabase

    trait EdgesetDatabase extends Database {

      /**
       * Core method for query methods, takes an predicate
       * and return a Set[Edge] that satisfies predicate
       */
      def edgeSet(predicate: PartialFunction[Edge, Boolean]): Set[Edge]

      /**
       * Query Methods
       */
      def findOutgoing(from: Node): Set[Edge] =
        edgeSet {
          case Edge(from2, _, _) => from == from2
        }

      def findIngoing(to: Node): Set[Edge] =
        edgeSet {
          case Edge(_, to2, _) => to == to2
        }

      def findOutgoing(from: Node, relationtype: RelationType): Set[Edge] =
        edgeSet {
          case Edge(from2, _, rel) => from == from2 && rel == relationtype
        }

      def findIngoing(to: Node, relationtype: RelationType): Set[Edge] =
        edgeSet {
          case Edge(_, to2, rel) => to == to2 && rel == relationtype
        }

      def findBetween(from: Node, to: Node): Set[Edge] =
        edgeSet {
          case Edge(from2, to2, _) => from == from2 && to == to2
        }

      def findAll(node: Node): Set[Edge] =
        edgeSet {
          case Edge(from, to, _) => node == from || node == to
        } // or findIngoing & findOutgoing

      def findAll(relationtype: RelationType): Set[Edge] =
        edgeSet {
          case Edge(_, _, rel) => rel == relationtype
        }

      def exists(edge: Edge): Option[Edge] =
        edgeSet {
          case e: Edge => e == edge
        } headOption


      /**
       * Update Methods
       */
       def update(oldNode: Node, newNode: Node): Set[Edge] =
        for {
          e @ Edge(to, from, rel) <- findAll(oldNode)
          newTo = if (to == oldNode) newNode else to
          newFrom = if (from == oldNode) newNode else from

          removed <- remove(e)
          added <- add(Edge(newTo, newFrom, rel))
        } yield added

       def update(oldEdge: Edge, newEdge: Edge): Option[Edge] =
         for {
          removed <- remove(oldEdge)
          added <- add(newEdge)
         } yield added

    }
  }
}
