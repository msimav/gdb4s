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
      // def idx = index("idx_triple", (from, relationtype, to), unique = true)

      def apply(from: String, rel: String, to: String): Edge =
        Edge(Node(from), Node(to), RelationType(rel))

      def unapply(e: Edge): Option[(String, String, String)] = e match {
        case Edge(Node(from), Node(to), RelationType(rel)) => Some((from, rel, to))
        case _ => None
      }
    }

    class SlickDatabase extends Database {

      private val q = Query(Triples)
      private def qFrom(from: Node) = q filter { _.from === from.id }
      private def qTo(to: Node) = q filter { _.to === to.id }
      private def qExists(edge: Edge) = {
        val Edge(Node(from), Node(to), RelationType(rel)) = edge
        q filter { triple =>
          triple.from === from && triple.to === to && triple.relationtype === rel
        }
      }

      private def setQuery(query: Query[Triples.type, Edge]): Set[Edge] = {
        try {
          query.to[Set]
        } catch {
          case e: Exception => Set.empty
        }
      }

      if (MTable.getTables.list.isEmpty) {
        slick withSession {
          Triples.ddl.create
        }
      }

      /**
       * Query Methods
       */
      def findOutgoing(from: Node): Set[Edge] = setQuery(qFrom(from))

      def findIngoing(to: Node): Set[Edge] = setQuery(qTo(to))

      def findOutgoing(from: Node, relationtype: RelationType): Set[Edge] =
        setQuery(q filter { triple => triple.from === from.id && triple.relationtype === relationtype.name })

      def findIngoing(to: Node, relationtype: RelationType): Set[Edge] =
        setQuery(q filter { triple => triple.to === to.id && triple.relationtype === relationtype.name })

      def findBetween(from: Node, to: Node): Set[Edge] =
        setQuery(q filter { triple => triple.from === from.id && triple.to === to.id })

      def findAll(node: Node): Set[Edge] =
        setQuery(q filter { triple => triple.from === node.id || triple.to === node.id })

      def findAll(relationtype: RelationType): Set[Edge] =
        setQuery(q filter { triple => triple.relationtype === relationtype.name })

      def exists(edge: Edge): Option[Edge] = {
        try {
          qExists(edge).firstOption
        } catch {
          case e: Exception => None
        }
      }

      /**
       * Add Methods
       */
       def add(node: Node): Option[Node] = None
       def add(edge: Edge): Option[Edge] = {
        try {
          Triples insert edge
          Some(edge)
        } catch {
          case e: Exception => None
        }
       }

       /**
       * Remove Methods
       */
       def remove(node: Node): Set[Edge] = {
        try {
          val query = q filter { triple => triple.from === node.id || triple.to === node.id }
          val result = query.to[Set]
          query.delete
          result
        } catch {
          case e: Exception => Set.empty
        }
       }
       def remove(edge: Edge): Option[Edge] = {
        try {
          val query = qExists(edge)
          val result = query.firstOption
          query.delete
          result
        } catch {
          case e: Exception => None
        }
       }

       /**
       * Update Methods
       */
       def update(oldNode: Node, newNode: Node): Set[Edge] ={
        try {
          qFrom(oldNode).map(_.from).update(newNode.id)
          qTo(oldNode).map(_.to).update(newNode.id)

          (qFrom(newNode) union qTo(newNode)).to[Set]
        } catch {
          case e: Exception => Set.empty
        }

       }
       def update(oldEdge: Edge, newEdge: Edge): Option[Edge] = {
        try {
          qExists(oldEdge).update(newEdge)
          qExists(newEdge).firstOption
        } catch {
          case e: Exception => None
        }
       }

    }
  }
}