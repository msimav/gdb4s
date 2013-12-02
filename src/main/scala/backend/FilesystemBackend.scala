package ms.tobbetu.gdb4s.backend

import scala.language.postfixOps
import scala.language.implicitConversions

import java.io.{File, FileWriter}
import java.io.File.{ separatorChar => / }
import scala.io.Source.fromFile

import spray.json._

import ms.tobbetu.gdb4s.Models._
import GraphJsonProtocol._
import ms.tobbetu.gdb4s.backend.EdgesetBackend._

/**
 * This backends implements very basic filesystem based database
 */
package FilesystemBackend {

  trait FilesystemStore extends EdgesetBackend {

    class FilesystemDatabase(path: File) extends EdgesetDatabase {
      require((path.exists && path.isDirectory) || path.mkdirs)

      def edgeSet(predicate: PartialFunction[Edge, Boolean]) =
        for {
         file <- dbFiles
         src = fromFile(file)
         str = src.getLines.mkString
         closeHack = src.close()
         edge = str.asJson.convertTo[Edge]
         if predicate(edge)
        } yield edge

      /**
       * Add Methods
       */
       def add(node: Node): Option[Node] = None
       def add(edge: Edge): Option[Edge] = {
        val edgeFile = fileForObject(edge)
        val fileWriter = new FileWriter(edgeFile)
        try {
          fileWriter.write(edge.toJson.compactPrint)
          Some(edge)
        } catch {
          case _ : Throwable => None
        } finally {
          fileWriter.close()
        }
       }


       /**
       * Remove Methods
       */
       def remove(node: Node): Set[Edge] =
        for {
          edge <- findAll(node)
          deletedEdge <- remove(edge)
        } yield deletedEdge

       def remove(edge: Edge): Option[Edge] = {
        val edgeFile = fileForObject(edge)
        if (edgeFile.delete) Some(edge)
        else None
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

    }
  }
}