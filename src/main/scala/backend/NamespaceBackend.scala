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
      abstract override def findOutgoing(from: Node): Set[Edge] =
        findWithNamespaces(super.findOutgoing(from))

      abstract override def findIngoing(to: Node): Set[Edge] =
        findWithNamespaces(super.findIngoing(to))

      abstract override def findOutgoing(from: Node, relationtype: RelationType): Set[Edge] =
        findWithNamespaces(super.findOutgoing(from, relationtype))

      abstract override def findIngoing(to: Node, relationtype: RelationType): Set[Edge] =
        findWithNamespaces(super.findIngoing(to, relationtype))

      abstract override def findBetween(from: Node, to: Node): Set[Edge] =
        findWithNamespaces(super.findBetween(from, to))

      abstract override def findAll(node: Node): Set[Edge] =
        findWithNamespaces(super.findAll(node))

      abstract override def findAll(relationtype: RelationType): Set[Edge] =
        findWithNamespaces(super.findAll(relationtype))


      private def findWithNamespaces(values: Set[Edge]): Set[Edge] = {
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