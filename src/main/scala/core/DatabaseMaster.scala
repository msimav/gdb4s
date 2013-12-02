package ms.tobbetu.gdb4s.core

import akka.actor.{ Actor, ActorLogging, Props, PoisonPill }

import Messages._

package object DatabaseMaster {
  trait DatabaseMasterConfig {
    val childProps: Props
  }

  trait WorkerExtention extends Actor with ActorLogging {
    abstract override def receive: Actor.Receive = {
      case msg => {
        super.receive(msg)
        tellParent(msg)
        log.info(s"$msg handled!")
      }
    }

    private def tellParent: Actor.Receive = {
      case msg: DatabaseMessage => context.parent ! WorkDone(self, msg)
      case unexpected => log.error(s"Unexpected msg: $unexpected")
    }
  }

  abstract class DatabaseMaster extends Actor with ActorLogging { this: DatabaseMasterConfig =>

    def receive = {
      case msg: DatabaseMessage => {
        val worker = context.actorOf(childProps)
        worker forward msg
        log.info(s"$msg assigned to $worker")
      }
      case WorkDone(worker, _) => context stop worker
    }
  }
}