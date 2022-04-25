package cql4s

import cats.effect.IO
import cql4s.dsl.*
import cql4s.test.model.{Event, Metadata, User}
import org.scalatest.Assertion
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Instant, LocalDate, LocalTime, ZoneOffset}
import java.util.UUID

class AggregationIntegrationSpec extends AnyFreeSpec with Matchers with CassandraAware:
  import cql4s.test.schema.events

  val insertEvent: Command[Event] = Insert.into(events).compile.pcontramap[Event]

  "count(*), count(id)" in {
    val startTime = Instant.now()
    val id1 = UUID.randomUUID()
    val id2 = UUID.randomUUID()
    val id3 = UUID.randomUUID()

    val metadata = Metadata(Instant.now(), None, User("me", None, (44, "1234567")))

    val countEvents_* = Select
      .from(events)
      .take(_ => count(*))
      .where(_("start_time") === :?)
      .allowFiltering(true)
      .compile

    val countEvents_id = Select
      .from(events)
      .take(e => count(e("id")))
      .where(_("start_time") === :?)
      .allowFiltering(true)
      .compile

    (for {
      given _ <- cassandra

      _ <- insertEvent.execute(Event(id1, "a", startTime, Nil, Map.empty, Set.empty, metadata))
      _ <- insertEvent.execute(Event(id2, "b", startTime, Nil, Map.empty, Set.empty, metadata))
      _ <- insertEvent.execute(Event(id3, "c", startTime.plusSeconds(1), Nil, Map.empty, Set.empty, metadata))

      result1 <- countEvents_*.one(startTime)
      result2 <- countEvents_id.one(startTime)
    } yield result1 -> result2).unsafeRunSync() shouldBe (2L, 2L)
  }

