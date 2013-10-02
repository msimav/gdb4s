import org.scalatest._

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.InMemoryBackend._

class InMemoryStoreSpec extends FlatSpec with Matchers {

	"InMemoryStore when initialized" should "be empty" in {
		val backend = new InMemoryStore

		assert(backend.edges.isEmpty)

		assert(backend.db.findOutgoing(Node('mustafa)) == Set())
		assert(backend.db.findIngoing(Node('mustafa)) == Set())
	}

	"InMemoryStore when filled" should "find Node(mustafa) in ingoing and outgoing relations" in {
		val backend = new InMemoryStore
		filldb(backend)

		val ingoing = for {
			Edge(_, to, _) <- backend.db.findIngoing('mustafa)
			} yield to
		val outgoing = for {
			Edge(from, _, _) <- backend.db.findOutgoing('mustafa)
			} yield from

		assert(ingoing.toSet ++ outgoing.toSet == Set(Node('mustafa)))
	}

	it should "find that mustafa know java, scala and python" in {
		val backend = new InMemoryStore
		filldb(backend)

		val results = backend.db.findOutgoing('mustafa, 'know)
		val objects = for(Edge(_, out, _) <- results) yield out

		assert(objects.toSet == Set(Node('java), Node('scala), Node('python)))
	}

	it should "find that mustafa love scala and python" in {
		val backend = new InMemoryStore
		filldb(backend)

		val results = backend.db.findOutgoing('mustafa, 'love)
		val objects = for(Edge(_, out, _) <- results) yield out

		assert(objects.toSet == Set(Node('scala), Node('python)))
	}

	it should "find that mustafa hate java" in {
		val backend = new InMemoryStore
		filldb(backend)

		val result = backend.db.findOutgoing('mustafa, 'hate)

		assert(result == Set(Edge('mustafa, 'java, 'hate)))
	}

	it should "find that scala loved by mustafa and odersky" in {
		val backend = new InMemoryStore
		filldb(backend)

		val results = backend.db.findIngoing('scala, 'love)
		val objects = for(Edge(in, _, _) <- results) yield in

		assert(objects.toSet == Set(Node('odersky), Node('mustafa)))
	}

	it should "find that mustafa and odersky love scala and mustafa love python" in {
		val backend = new InMemoryStore
		filldb(backend)

		val results = backend.db.findAll(RelationType('love))
		val expected: Set[Edge] = Set('mustafa -> 'love -> 'scala,
			'odersky -> 'love -> 'scala,
			'mustafa -> 'love -> 'python)

		assert(results == expected)
	}

	def filldb(value: InMemoryStore) {
		value.db.add('mustafa -> 'know -> 'scala)
		value.db.add('mustafa -> 'love -> 'scala)
		value.db.add('odersky -> 'love -> 'scala)

		value.db.add('mustafa -> 'know -> 'python)
		value.db.add('mustafa -> 'love -> 'python)

		value.db.add('mustafa -> 'know -> 'java)
		value.db.add('mustafa -> 'hate -> 'java)
	}
}
