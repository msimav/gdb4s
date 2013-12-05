package ms.tobbetu.gdb4s.backend

import slick.driver.H2Driver.simple._
import slick.session.Session

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.EdgesetBackend._

package SlickBackend {

  trait H2Store extends SlickStore {

    val slick = Database.forURL("jdbc:h2:file:/home/mustafa/.gdb4s/database;LOCK_MODE=0",
                                driver = "org.h2.Driver")
    implicit val session: Session = slick.createSession()

  }
}