import org.scalatest._

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.InMemoryBackend._

class InMemorySpec extends FlatSpec with Matchers {

	"InMemoryBackend" should "not find Node(mustafa)" in {
		val backend = new InMemory
		assert(backend.finder.find(Node("mustafa")) == None)
	}
}