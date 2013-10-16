import org.scalatest._

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.Backend.DatabaseBackend
import ms.tobbetu.gdb4s.backend.InMemoryBackend.InMemoryStore
import ms.tobbetu.gdb4s.backend.FilesystemBackend.FilesystemStore

trait BackendSpec { this: FlatSpec =>

	def emptyDatabase(backend: => DatabaseBackend) {

		it should "not find any Node(mustafa)" in {
			expect(Set.empty[Edge]) { backend.db.findOutgoing('mustafa) }
			expect(Set.empty[Edge]) { backend.db.findIngoing('mustafa) }
			expect(Set.empty[Edge]) { backend.db.findAll(Node('mustafa)) }
		}

		it should "not find any RelationType(love)" in {
			expect(Set.empty[Edge]) { backend.db.findAll(RelationType('mustafa)) }
		}

		it should "find mustafa love scala after insert" in {
			expect(Set.empty[Edge]) { backend.db.findOutgoing('mustafa, 'love) }

			backend.db.add('mustafa -> 'love -> 'scala)

			expect(Set[Edge]('mustafa -> 'love -> 'scala)) {
				backend.db.findOutgoing('mustafa, 'love)
			}
		}

	}

	def nonEmptyDatabase(backend: => DatabaseBackend) {

		it should "find Node(mustafa) in outgoing relations" in {

			expect(Set(Node('mustafa))) {
				for {
					Edge(from, _, _) <- backend.db.findOutgoing('mustafa)
				} yield from
			}

		}

		it should "find Node(scala) in ingoing relations" in {

			expect(Set[Node]('scala)) {
				for {
					Edge(_, to, _) <- backend.db.findIngoing('scala)
				} yield to
			}

		}

		it should "find that mustafa know java, scala and python" in {

			expect(Set(Node('java), Node('scala), Node('python))) {
				for {
					Edge(_, out, _) <- backend.db.findOutgoing('mustafa, 'know)
				} yield out
			}

		}

		it should "find that mustafa love scala and python" in {

			expect(Set(Node('scala), Node('python))) {
				for {
					Edge(_, out, _) <- backend.db.findOutgoing('mustafa, 'love)
				} yield out
			}

		}

		it should "find that mustafa hate java" in {

			expect(Set(Edge('mustafa, 'java, 'hate))) {
				backend.db.findOutgoing('mustafa, 'hate)
			}

		}

		it should "find that scala loved by mustafa and odersky" in {

			expect(Set(Node('odersky), Node('mustafa))) {
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

			expect(expected) {
				backend.db.findAll(RelationType('love))
			}

		}

		it should "replace mustafa with msimav" in {

			val beforeUpdate: Set[Edge] = Set(
				'mustafa -> 'love -> 'scala,
				'mustafa -> 'love -> 'python)

			val afterUpdate: Set[Edge] = Set(
				'msimav -> 'love -> 'scala,
				'msimav -> 'love -> 'python)

			expect(beforeUpdate) {
				backend.db.findOutgoing('mustafa, 'love)
			}

			expect(Set.empty[Edge]) {
				backend.db.findOutgoing('msimav, 'love)
			}

			// Update
			backend.db.update('mustafa, 'msimav)

			expect(Set.empty[Edge]) {
				backend.db.findOutgoing('mustafa, 'love)
			}

			expect(afterUpdate) {
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

			expect(beforeUpdate) {
				backend.db.findOutgoing('mustafa, 'love)
			}

			// Update
			backend.db.update(
				'mustafa -> 'love -> 'python,
				'mustafa -> 'love -> 'ruby)

			expect(afterUpdate) {
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