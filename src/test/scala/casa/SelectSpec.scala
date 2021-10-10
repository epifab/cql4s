package casa

import casa.compiler.{Command, Query}
import cats.data.Chain.Singleton
import cats.effect.unsafe.IORuntime
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class SelectSpec extends AnyFreeSpec with Matchers with CassandraAware:

  implicit val ioRuntime: IORuntime = IORuntime.global

  object events extends Table[
    "events",
    (
      "id" :=: uuid,
      "artists" :=: list[varchar],
      "start_time" :=: timestamp,
      "venue" :=: nullable[text],
      "prices" :=: map[varchar, decimal],
      "tags" :=: set[varchar]
    )
  ]

  val insert: Command[(UUID, Instant, List[String], Option[String], Map[String, BigDecimal], Set[String])] =
    Insert
      .into(events)
      .fields(g => (
        g("id"),
        g("start_time"),
        g("artists"),
        g("venue"),
        g("prices"),
        g("tags")
      ))
      .compile

  val select: Query[EmptyTuple, (UUID, Instant, List[String], Option[String], Map[String, BigDecimal], Set[String])] =
    Select
      .from(events)
      .take(g => (
        g("id"),
        g("start_time"),
        g("artists"),
        g("venue"),
        g("prices"),
        g("tags")
      ))
      .compile

  "Can insert / retrieve data" in {

    val event = (
      UUID.randomUUID(),
      Instant.now().truncatedTo(ChronoUnit.MILLIS),
      List("Radiohead", "Sigur Ros"),
      Some("Roundhouse"),
      Map(
        "USD" -> BigDecimal(20.5),
        "GBP" -> BigDecimal(16)
      ),
      Set("rock", "post rock", "indie")
    )

    cassandraRuntime.use(cassandra =>
      for {
        _ <- cassandra.execute("TRUNCATE events", List.empty)
        _ <- cassandra.execute(insert)(event)
        result <- cassandra.execute(select)(Tuple()).compile.toList
      } yield result
    ).unsafeRunSync() shouldBe List(event)

  }
