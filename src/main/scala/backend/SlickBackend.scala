package ms.tobbetu.gdb4s.backend

import slick.driver.BasicDriver.simple._
import slick.session.Session
import slick.session.{ Database => SlickDB }
import scala.slick.jdbc.meta.MTable

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.Backend._

/**
 * This backends implements abstract database
 * that provides base class for slick based backends
 */
package SlickBackend {

  trait SlickStore extends DatabaseBackend {

    val db: SlickDatabase

    val slick: SlickDB
    implicit val session: Session

    object Triples extends Table[Edge]("TRIPLES") {
      def * = from ~ relationtype ~ to <> (apply _, unapply _)

      def from = column[String]("FROM")
      def to = column[String]("TO")
      def relationtype = column[String]("RELATION")

      def pk = primaryKey("pk_triple", (from, relationtype, to))
      def idx = index("idx_triple", (from, relationtype, to), unique = true)

      def apply(from: String, rel: String, to: String): Edge =
        Edge(Node(from), Node(to), RelationType(rel))

      def unapply(e: Edge): Option[(String, String, String)] = e match {
        case Edge(Node(from), Node(to), RelationType(rel)) => Some((from, rel, to))
        case _ => None
      }
    }

    class SlickDatabase extends Database {

      if (MTable.getTables.list.isEmpty) {
        slick withSession {
          Triples.ddl.create
        }
      }

      /**
       * Query Methods
       */
      def findOutgoing(from: Node): Set[Edge] = {
        val q = for {
          triple <- Triples
          if (triple.from === from.id)
        } yield triple
        q.to[Set]
      }

      def findIngoing(to: Node): Set[Edge] = {
        val q = for {
          triple <- Triples
          if (triple.to === to.id)
        } yield triple
        q.to[Set]
      }

      def findOutgoing(from: Node, relationtype: RelationType): Set[Edge] = {
        val q = for {
          triple <- Triples
          if (triple.from === from.id)
          if (triple.relationtype === relationtype.name)
        } yield triple
        q.to[Set]
      }

      def findIngoing(to: Node, relationtype: RelationType): Set[Edge] = {
        val q = for {
          triple <- Triples
          if (triple.to === to.id)
          if (triple.relationtype === relationtype.name)
        } yield triple
        q.to[Set]
      }

      def findBetween(from: Node, to: Node): Set[Edge] = {
        val q = for {
          triple <- Triples
          if (triple.from === from.id)
          if (triple.to === to.id)
        } yield triple
        q.to[Set]
      }

      def findAll(node: Node): Set[Edge] = {
        val q = for {
          triple <- Triples
          if (triple.from === node.id || triple.to === node.id)
        } yield triple
        q.to[Set]
      }

      def findAll(relationtype: RelationType): Set[Edge] = {
        val q = for {
          triple <- Triples
          if (triple.relationtype === relationtype.name)
        } yield triple
        q.to[Set]
      }

      def exists(edge: Edge): Option[Edge] = {
        val Edge(Node(from), Node(to), RelationType(rel)) = edge
        val q = for {
          triple <- Triples
          if (triple.from === from)
          if (triple.to === to)
          if (triple.relationtype === rel)
        } yield triple
        q.firstOption
      }

      /**
       * Add Methods
       */
       def add(node: Node): Option[Node] = None
       def add(edge: Edge): Option[Edge] = {
        Triples.insert(edge)
        Some(edge)
       }

       /**
       * Remove Methods
       */
       def remove(node: Node): Set[Edge] = {
        val q = for {
          triple <- Triples
          if (triple.from === node.id || triple.to === node.id)
        } yield triple
        q.delete
        q.to[Set]
       }
       def remove(edge: Edge): Option[Edge] = {
        val Edge(Node(from), Node(to), RelationType(rel)) = edge
        val q = for {
          triple <- Triples
          if (triple.from === from)
          if (triple.to === to)
          if (triple.relationtype === rel)
        } yield triple
        q.delete
        q.firstOption
       }

       /**
       * Update Methods
       */
       def update(oldNode: Node, newNode: Node): Set[Edge] = {
        val qFrom = for {
          triple <- Triples
          if (triple.from === oldNode.id)
        } yield triple.from
        qFrom.update(newNode.id)

        val qTO = for {
          triple <- Triples
          if (triple.to === oldNode.id)
        } yield triple.to
        qTO.update(newNode.id)

        findAll(newNode)
       }
       def update(oldEdge: Edge, newEdge: Edge): Option[Edge] = {
        val Edge(Node(from), Node(to), RelationType(rel)) = oldEdge
        val q = for {
          triple <- Triples
          if (triple.from === from)
          if (triple.to === to)
          if (triple.relationtype === rel)
        } yield triple
        q.update(newEdge)
        q.firstOption
       }

    }
  }
}