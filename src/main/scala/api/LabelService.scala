package ms.tobbetu.gdb4s.api

import spray.routing._
import spray.json._
import spray.httpx.SprayJsonSupport._

import akka.pattern.ask
import akka.util.Timeout

import ms.tobbetu.gdb4s.Models._
import GraphJsonProtocol._
import ms.tobbetu.gdb4s.core.Messages._

trait LabelService extends ApiService {
  val labelRoute = get {
    path(Segment) { str =>
      complete {
            (backend ? Label(str)).mapTo[Set[Edge]]
      }
    }
  }
}