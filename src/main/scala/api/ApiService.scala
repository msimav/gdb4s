package ms.tobbetu.gdb4s.api

import scala.language.postfixOps
import scala.language.implicitConversions
import reflect.ClassTag

import spray.routing._
import spray.json._
import spray.httpx.SprayJsonSupport._
import spray.http.{ HttpEntity, ContentTypes }
import spray.http.StatusCodes._

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Future

import ms.tobbetu.gdb4s.Models._
import GraphJsonProtocol._
import ms.tobbetu.gdb4s.core.Messages._

trait ApiService extends HttpService {

  val backend: ActorRef
  implicit val timeout: Timeout
  implicit val executionContext = actorRefFactory.dispatcher

  implicit val eh: ExceptionHandler = ExceptionHandler.default
  implicit val rs: RoutingSettings = RoutingSettings.default

  private implicit def str2optionNode(value: String): Option[Node] =
    if (value == "-") None
    else Some(Node(value))

  private implicit def str2optionRel(value: String): Option[RelationType] =
    if (value == "-") None
    else Some(RelationType(value))

  val frontend = getFromResourceDirectory("frontend") ~ redirect("/index.html", MovedPermanently)

  val dbRoute = get {
      path(Segments) {
        case Nil => complete(BadRequest)
        case from :: Nil => query(Query(from, None, None))
        case from :: rel :: Nil => query(Query(from, None, rel))
        case from :: rel :: to :: Nil => query(Query(from, to, rel))
        case _ => complete(BadRequest)
      }
    } ~
    post {
      path(Segments) {
        case node :: Nil => complete {
          val query = Add(Left(node))
          (backend ? query).mapTo[Option[Node]]
        }
        case from :: rel :: to :: Nil => complete {
          val query = Add(Right(from -> rel -> to))
          (backend ? query).mapTo[Option[Edge]]
        }
        case _ => complete(BadRequest)
      }
    } ~
    delete {
      path(Segments) {
        case node :: Nil => complete {
          val query = Remove(Left(node))
          (backend ? query).mapTo[Set[Edge]]
        }
        case from :: rel :: to :: Nil => complete {
          val query = Remove(Right(from -> rel -> to))
          (backend ? query).mapTo[Option[Edge]]
        }
        case _ => complete(BadRequest)
      }
    } ~
    put {
      path(Segments) {
        case oldNode :: Nil => entity(as[Node]) { newNode =>
          complete {
            val query = Update(Left(oldNode), Left(newNode))
            (backend ? query).mapTo[Set[Edge]]
          }
        }
        case from :: rel :: to :: Nil => entity(as[Edge]) { newEdge =>
          complete {
            val query = Update(Right(from -> rel -> to), Right(newEdge))
            (backend ? query).mapTo[Option[Edge]]
          }
        }
        case _ => complete(BadRequest)
      }
    }

    val batchRoute =
      post {
        entity(as[List[Edge]]) { list =>
          complete {
            val success = for {
              edge <- list
            } yield (backend ? Add(Right(edge))).mapTo[Option[Edge]]
            Future.sequence(success)
          }
        }
      }

    def query(msg: Query) = msg match {
      case Query(None, None, None) => complete(BadRequest)
      case Query(Some(_), Some(_), Some(_)) => complete { (backend ? msg).mapTo[Option[Edge]] }
      case _ => complete { (backend ? msg).mapTo[Set[Edge]] }
    }

}