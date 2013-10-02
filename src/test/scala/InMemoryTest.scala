import org.scalatest._

import ms.tobbetu.gdb4s.Models._
import ms.tobbetu.gdb4s.backend.InMemoryBackend._

class InMemorySpec extends FlatSpec with Matchers {

	"InMemoryBackend when initialized" should "be empty" in {
		val backend = new InMemory

		assert(backend.nodes.isEmpty)
		assert(backend.edges.isEmpty)

		assert(backend.finder.find(Node("mustafa")) == None)
	}

	"InMemoryBackend when filled" should "find Node(mustafa)" in {
		val backend = new InMemory
		filldb(backend)

		assert(backend.finder.find(Node("mustafa")) == Some(Node("mustafa")))
	}

	it should "find that mustafa know java, scala and python" in {
		val backend = new InMemory
		filldb(backend)

		val results = backend.finder.find(Node("mustafa"), RelationType("know"))
		val objects = for(Edge(_, out, _) <- results) yield out

		assert(objects.toSet == Set(Node("java"), Node("scala"), Node("python")))
	}

	it should "find that mustafa love scala and python" in {
		val backend = new InMemory
		filldb(backend)

		val results = backend.finder.find(Node("mustafa"), RelationType("love"))
		val objects = for(Edge(_, out, _) <- results) yield out

		assert(objects.toSet == Set(Node("scala"), Node("python")))
	}

	it should "find that mustafa hate java" in {
		val backend = new InMemory
		filldb(backend)

		val result = backend.finder.find(Node("mustafa"), RelationType("hate"))

		assert(result == Set(Edge(Node("mustafa"), Node("java"), RelationType("hate"))))
	}

	it should "find that mustafa and odersky love scala and mustafa love python" in {
		val backend = new InMemory
		filldb(backend)

		val results = backend.finder.find(RelationType("love"))
		val expected: Set[Edge] = Set("mustafa" -> "love" -> "scala",
			"odersky" -> "love" -> "scala",
			"mustafa" -> "love" -> "python")

		assert(results == expected)
	}

	def filldb(value: InMemory) {
		value.nodes.add("mustafa")
		value.nodes.add("odersky")


		value.nodes.add("scala")
		value.nodes.add("python")
		value.nodes.add("java")

		value.edges.add("mustafa" -> "know" -> "scala")
		value.edges.add("mustafa" -> "love" -> "scala")
		value.edges.add("odersky" -> "love" -> "scala")

		value.edges.add("mustafa" -> "know" -> "python")
		value.edges.add("mustafa" -> "love" -> "python")

		value.edges.add("mustafa" -> "know" -> "java")
		value.edges.add("mustafa" -> "hate" -> "java")
	}
}
