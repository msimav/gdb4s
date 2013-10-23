package ms.tobbetu.gdb4s.backend

import java.io.{File, FileWriter}
import java.io.File.{ separatorChar => / }
import scala.io.Source.fromFile

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.Backend._

/**
 * This backends implements very basic filesystem based database
 */
package FilesystemBackend {

  class FilesystemStore(path: File) extends DatabaseBackend {
    require((path.exists && path.isDirectory) ||
      (path.delete && path.mkdirs))

    val db = new FilesystemDatabase

    class FilesystemDatabase() extends Database {

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

      def findAll(node: Node): Set[Edge] =
        edgeSet {
          case Edge(from, to, _) => node == from || node == to
        } // or findIngoing & findOutgoing

      def findAll(relationtype: RelationType): Set[Edge] =
        edgeSet {
          case Edge(_, _, rel) => rel == relationtype
        }


      /**
       * Add Methods
       */
       def add(node: Node): Unit = ()
       def add(edge: Edge): Unit = {
        val edgeFile = fileForObject(edge)
        val fileWriter = new FileWriter(edgeFile)
        try {
          fileWriter.write(edge.asJson)
        } finally {
          fileWriter.close()
        }
       }


       /**
       * Remove Methods
       */
       def remove(node: Node): Unit =
        for {
          edge <- findAll(node)
        } remove(edge)

       def remove(edge: Edge): Unit = {
        val edgeFile = fileForObject(edge)
        edgeFile.delete
       }


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


      /**
       * Helper Methods
       */
       private def hash(obj: GraphObject) = {
        import java.security.MessageDigest

        val md = MessageDigest.getInstance("SHA-256")
        val objAsBytes = obj.toString.getBytes("UTF-8")
        val digestBytes = md.digest(objAsBytes)
        digestBytes map { "%02x" format _ } mkString
       }

       private def fileForObject(obj: GraphObject) =
         new File(path.getAbsolutePath + / + hash(obj))

       private def dbFiles = path.listFiles.toSet

       private def edgeSet(predicate: PartialFunction[Edge, Boolean]) =
        for {
         file <- dbFiles
         str = fromFile(file).getLines.mkString
         edge = GraphObject.toEdge(str)
         if predicate(edge)
        } yield edge

    }
  }
}