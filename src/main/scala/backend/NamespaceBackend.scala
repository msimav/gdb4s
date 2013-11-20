package ms.tobbetu.gdb4s.backend

import ms.tobbetu.gdb4s.Models._

package Backend {

  /**
   * NamespacedBackend trait defines a stackable trait that introduces
   * namespaces feature to existing backend implementations. It designed
   * in stackable fasion in order to be removed when there is no need for
   * namespaces.
   */
  trait NamespacedBackend extends DatabaseBackend {
    trait NamespacedDatabase extends Database {

      /**
       * Query Methods
       */
      abstract override def findOutgoing(from: Node): Set[Edge] = {
        val values = super.findOutgoing(from)
        val namespaces = for (v <- values) yield findNamespace(v)
        namespaces.flatten ++ values
      }
      abstract override def findIngoing(to: Node): Set[Edge] = {
        val values = super.findIngoing(to)
        val namespaces = for (v <- values) yield findNamespace(v)
        namespaces.flatten ++ values
      }
      abstract override def findOutgoing(from: Node, relationtype: RelationType): Set[Edge] = {
        val values = super.findOutgoing(from, relationtype)
        val namespaces = for (v <- values) yield findNamespace(v)
        namespaces.flatten ++ values
      }
      abstract override def findIngoing(to: Node, relationtype: RelationType): Set[Edge] = {
        val values = super.findIngoing(to, relationtype)
        val namespaces = for (v <- values) yield findNamespace(v)
        namespaces.flatten ++ values
      }
      abstract override def findBetween(from: Node, to: Node): Set[Edge] = {
        val values = super.findBetween(from, to)
        val namespaces = for (v <- values) yield findNamespace(v)
        namespaces.flatten ++ values
      }
      abstract override def findAll(node: Node): Set[Edge] = {
        val values = super.findAll(node)
        val namespaces = for (v <- values) yield findNamespace(v)
        namespaces.flatten ++ values
      }
      abstract override def findAll(relationtype: RelationType): Set[Edge] = {
        val values = super.findAll(relationtype)
        val namespaces = for (v <- values) yield findNamespace(v)
        namespaces.flatten ++ values
      }

      private def findNamespace(e: Edge): Set[Edge] = {
        val Edge(Node(from), Node(to), RelationType(rel)) = e
        val namepaceOptions = Set(nsOption(from), nsOption(to), nsOption(rel))
        val namespaces = for {
          elem <- namepaceOptions
          ns <- elem
        } yield ns
        namespaces flatMap { x => super.findOutgoing(Node(x), RelationType("rdf:namespace")) }
      }

      private def nsOption(s: String): Option[String] = {
        val split = s.split(":", 2)
        if (split.length == 2) Some(split(0))
        else None
      }
    }
  }

}