package ms.tobbetu.gdb4s.api

import scala.language.implicitConversions

import spray.routing._
import spray.json._
import spray.httpx.SprayJsonSupport._

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Future

import ms.tobbetu.gdb4s.Models._
import GraphJsonProtocol._
import ms.tobbetu.gdb4s.core.Messages._

trait NamespaceService extends ApiService {

  val nsRelation = RelationType("rdf:namespace")

  val nsRoute = pathEnd {
        get {
          complete {
            (backend ? Query(None, None, Some(nsRelation))).mapTo[Set[Edge]]
          }
        }
      } ~
      path(Segment) { name =>
        get {
          complete {
            for {
              set <- (backend ? Query(Some(Node(name)), None, Some(nsRelation))).mapTo[Set[Edge]]
            } yield set.headOption
          }
        } ~
        post {
          entity(as[Node]) { node =>
            val edge = Edge(Node(name), node, nsRelation)
            complete { (backend ? Add(Right(edge))).mapTo[Option[Edge]] }
          }
        } ~
        delete {
          val query = (backend ? Query(Some(Node(name)), None, Some(nsRelation))).mapTo[Set[Edge]]
          val delete = for {
            set <- query
            result = if (set.isEmpty) Future(None)
                     else (backend ? Remove(Right(set.head))).mapTo[Option[Edge]]
          } yield result
          complete { delete }
        } ~
        put {
          entity(as[Node]) { node =>
            val query = (backend ? Query(Some(Node(name)), None, Some(nsRelation))).mapTo[Set[Edge]]
            val newEdge = Edge(Node(name), node, nsRelation)
            val update = for {
              set <- query
              result = if (set.isEmpty) Future(None)
                       else (backend ? Update(Right(set.head), Right(newEdge))).mapTo[Option[Edge]]
            } yield result
            complete { update }
          }
        }
      }

}