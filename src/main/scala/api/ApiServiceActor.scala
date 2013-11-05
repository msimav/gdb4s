package ms.tobbetu.gdb4s.api

import scala.language.postfixOps
import scala.language.implicitConversions
import reflect.ClassTag

import spray.routing._
import spray.json._
import DefaultJsonProtocol._
import spray.httpx.marshalling._
import spray.http.{ HttpEntity, ContentTypes }

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.core.Messages._

class ApiServiceActor(backend: ActorRef) extends HttpServiceActor {

  private implicit def str2optionNode(value: String): Option[Node] =
    if (value == "-") None
    else Some(Node(value))

  private implicit def str2optionRel(value: String): Option[RelationType] =
    if (value == "-") None
    else Some(RelationType(value))

  private implicit val timeout = Timeout(5 seconds)

  // private implicit val marshaller = Marshaller.of[Set[Edge]](ContentTypes.application/json) { (value, contentType, ctx) =>
  //     ctx.marshalTo(HttpEntity(contentType, value.toJson))
  //   }

  def receive = runRoute(pathPrefix("db") { dbRoute })

  val dbRoute = get {
      path(Segments) {
        case Nil => reject
        case from :: Nil => complete { queryBackend(Query(from, None, None)) }
        case from :: rel :: Nil => complete { queryBackend(Query(from, None, rel)) }
        case from :: rel :: to :: Nil => complete {
          val reply = backend ? Query(from, to, rel)
          for {
            result <- reply.mapTo[Option[Edge]]
          } yield (result.toString)
        }
        case _ => reject
      }
    } ~
    post {
      path(Segments) {
        case from :: rel :: to :: Nil => complete {
          val query = Add(Right(Symbol(from) -> Symbol(rel) -> Symbol(to)))
          backend ! query
          query.toString
        }
        case _ => complete { "Hatali Post Istegi!" }
      }
    }

    private def queryBackend[T](query: Query) = {
          val reply = backend ? query
          for {
            result <- reply.mapTo[Set[Edge]]
          } yield (result.toString)
        }
}