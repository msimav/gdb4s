import org.scalatest._

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.Backend.DatabaseBackend
import ms.tobbetu.gdb4s.backend.InMemoryBackend.InMemoryStore
import ms.tobbetu.gdb4s.backend.FilesystemBackend.FilesystemStore

trait BackendSpec { this: FlatSpec =>

	def emptyDatabase(backend: => DatabaseBackend) {

		it should "not find any Node(mustafa)" in {
			assertResult(Set.empty[Edge]) { backend.db.findOutgoing('mustafa) }
			assertResult(Set.empty[Edge]) { backend.db.findIngoing('mustafa) }
			assertResult(Set.empty[Edge]) { backend.db.findAll(Node("mustafa")) }
		}

		it should "not find any RelationType(love)" in {
			assertResult(Set.empty[Edge]) { backend.db.findAll(RelationType("mustafa")) }
		}

		it should "not contain mustafa love scala" in {
			assertResult(Option.empty[Edge]) {
				backend.db.exists('mustafa -> 'love -> 'scala)
			}
		}

		it should "find mustafa love scala after insert" in {
			assertResult(Option.empty[Edge]) {
				backend.db.exists('mustafa -> 'love -> 'scala)
			}

			assertResult(Some[Edge]('mustafa -> 'love -> 'scala)) {
				backend.db.add('mustafa -> 'love -> 'scala)
			}

			assertResult(Set[Edge]('mustafa -> 'love -> 'scala)) {
				backend.db.findOutgoing('mustafa, 'love)
			}

			assertResult(Some[Edge]('mustafa -> 'love -> 'scala)) {
				backend.db.exists('mustafa -> 'love -> 'scala)
			}
		}

	}

	def nonEmptyDatabase(backend: => DatabaseBackend) {

		it should "find Node(mustafa) in outgoing relations" in {

			assertResult(Set(Node("mustafa"))) {
				for {
					Edge(from, _, _) <- backend.db.findOutgoing('mustafa)
				} yield from
			}

		}

		it should "find Node(scala) in ingoing relations" in {

			assertResult(Set[Node]('scala)) {
				for {
					Edge(_, to, _) <- backend.db.findIngoing('scala)
				} yield to
			}

		}

		it should "find that mustafa know java, scala and python" in {

			assertResult(Set(Node("java"), Node("scala"), Node("python"))) {
				for {
					Edge(_, out, _) <- backend.db.findOutgoing('mustafa, 'know)
				} yield out
			}

		}

		it should "find that mustafa love scala and python" in {

			assertResult(Set(Node("scala"), Node("python"))) {
				for {
					Edge(_, out, _) <- backend.db.findOutgoing('mustafa, 'love)
				} yield out
			}

		}

		it should "find that mustafa hate java" in {

			assertResult(Set(Edge('mustafa, 'java, 'hate))) {
				backend.db.findOutgoing('mustafa, 'hate)
			}

		}

		it should "find that scala loved by mustafa and odersky" in {

			assertResult(Set(Node("odersky"), Node("mustafa"))) {
				for {
					Edge(in, _, _) <- backend.db.findIngoing('scala, 'love)
				} yield in
			}

		}

		it should "find that mustafa and odersky love scala and mustafa love python" in {

			val expected: Set[Edge] = Set(
				'mustafa -> 'love -> 'scala,
				'odersky -> 'love -> 'scala,
				'mustafa -> 'love -> 'python)

			assertResult(expected) {
				backend.db.findAll(RelationType("love"))
			}

		}

		it should "find relation between mustafa and scala" in {

			assertResult(Set(Edge('mustafa, 'scala, 'love), Edge('mustafa, 'scala, 'know))) {
				backend.db.findBetween('mustafa, 'scala)
			}

		}

		it should "contain mustafa love scala" in {
			assertResult(Some[Edge]('mustafa -> 'love -> 'scala)) {
				backend.db.exists('mustafa -> 'love -> 'scala)
			}
		}

		it should "not contain mustafa love java" in {
			assertResult(Option.empty[Edge]) {
				backend.db.exists('mustafa -> 'love -> 'java)
			}
		}

		it should "remove mustafa hate java" in {

			assertResult(Set[Edge]('mustafa -> 'hate -> 'java)) {
				backend.db.findOutgoing('mustafa, 'hate)
			}

			// Remove
			assertResult(Some[Edge]('mustafa -> 'hate -> 'java)) {
				backend.db.remove('mustafa -> 'hate -> 'java)
			}

			assertResult(Set.empty[Edge]) {
				backend.db.findOutgoing('mustafa, 'hate)
			}

			assertResult(Option.empty[Edge]) {
				backend.db.exists('mustafa -> 'hate -> 'java)
			}
		}

		it should "remove Node(odersky)" in {

			assertResult(Set[Edge]('odersky -> 'love -> 'scala)) {
				backend.db.findAll(Node("odersky"))
			}

			// Remove
			assertResult(Set[Edge]('odersky -> 'love -> 'scala)) {
				backend.db.remove('odersky)
			}

			assertResult(Set.empty[Edge]) {
				backend.db.findAll(Node("odersky"))
			}
		}

		it should "replace mustafa with msimav" in {

			val beforeUpdate: Set[Edge] = Set(
				'mustafa -> 'love -> 'scala,
				'mustafa -> 'love -> 'python)

			val afterUpdate: Set[Edge] = Set(
				'msimav -> 'love -> 'scala,
				'msimav -> 'love -> 'python)

			val updateResult: Set[Edge] = Set(
				'msimav -> 'know -> 'scala,
				'msimav -> 'love -> 'scala,
				'msimav -> 'know -> 'python,
				'msimav -> 'love -> 'python,
				'msimav -> 'know -> 'java,
				'msimav -> 'hate -> 'java)

			assertResult(beforeUpdate) {
				backend.db.findOutgoing('mustafa, 'love)
			}

			assertResult(Set.empty[Edge]) {
				backend.db.findOutgoing('msimav, 'love)
			}

			// Update
			assertResult(updateResult) {
				backend.db.update('mustafa, 'msimav)
			}

			assertResult(Set.empty[Edge]) {
				backend.db.findOutgoing('mustafa, 'love)
			}

			assertResult(afterUpdate) {
				backend.db.findOutgoing('msimav, 'love)
			}

		}

		it should "replace `mustafa love python` with `mustafa love ruby`" in {

			val beforeUpdate: Set[Edge] = Set(
				'mustafa -> 'love -> 'scala,
				'mustafa -> 'love -> 'python)

			val afterUpdate: Set[Edge] = Set(
				'mustafa -> 'love -> 'scala,
				'mustafa -> 'love -> 'ruby)

			assertResult(beforeUpdate) {
				backend.db.findOutgoing('mustafa, 'love)
			}

			// Update
			assertResult(Some[Edge]('mustafa -> 'love -> 'ruby)) {
				backend.db.update(
					'mustafa -> 'love -> 'python,
					'mustafa -> 'love -> 'ruby)
			}

			assertResult(afterUpdate) {
				backend.db.findOutgoing('mustafa, 'love)
			}

		}
	}

}

/**
 * Initial data for databases to test
 */
trait InitDatabase extends BeforeAndAfterEach { this: FlatSpec =>

	val backend: DatabaseBackend

	override def beforeEach() {
		backend.db.add('mustafa -> 'know -> 'scala)
		backend.db.add('mustafa -> 'love -> 'scala)
		backend.db.add('odersky -> 'love -> 'scala)

		backend.db.add('mustafa -> 'know -> 'python)
		backend.db.add('mustafa -> 'love -> 'python)

		backend.db.add('mustafa -> 'know -> 'java)
		backend.db.add('mustafa -> 'hate -> 'java)
	}
}

/**
 * Test Suites
 */

class EmptyInMemorySpec extends FlatSpec with BackendSpec {

	val backend = new InMemoryStore

	"InMemoryStore when empty" should behave like emptyDatabase(backend)

}

class NonEmptyInMemorySpec extends FlatSpec with BackendSpec with InitDatabase {

	val backend = new InMemoryStore

	"InMemoryStore when non-empty" should behave like nonEmptyDatabase(backend)

}

class EmptyFilesystemSpec extends FlatSpec with BackendSpec {

	val path = java.io.File.createTempFile("gdb4s","test")
	val backend = new FilesystemStore(path)

	"FilesystemStore when empty" should behave like emptyDatabase(backend)

}

class NonEmptyFilesystemSpec extends FlatSpec with BackendSpec with InitDatabase {

	val path = java.io.File.createTempFile("gdb4s","test")
	val backend = new FilesystemStore(path)

	"FilesystemStore when non-empty" should behave like nonEmptyDatabase(backend)

}