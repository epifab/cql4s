package casa

import cats.data.Chain.Singleton
import cats.effect.unsafe.IORuntime
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class SelectSpec extends AnyFreeSpec with Matchers with CassandraAware:

  implicit val ioRuntime: IORuntime = IORuntime.global

  object gigs extends Table["events", ("artists" :=: list[varchar], "start_time" :=: timestamp, "venue" :=: text)]

  // cassandra stores up to millis
  val atSomePoint = Instant.now().truncatedTo(ChronoUnit.MILLIS)

  val query =
    Select
      .from(gigs)
      .take("artists", "start_time", "venue")

  "Can run a simple select query" in {
    cassandraRuntime.use(cassandra =>
      for {
        _ <- cassandra.execute("TRUNCATE events", List.empty)
        _ <- cassandra.execute("INSERT INTO events (id, artists, venue, start_time) VALUES (?, ?, ?, ?)", List(UUID.randomUUID(), java.util.List.of("Radiohead", "Sigur Ros"), "Roundhouse", atSomePoint))
        result <- cassandra.execute(query.compile)(EmptyTuple).compile.toList
      } yield result
    ).unsafeRunSync() shouldBe List((List("Radiohead", "Sigur Ros"), atSomePoint, "Roundhouse"))
  }
