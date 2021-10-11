package cql4s

import cats.data.Chain.Singleton
import cats.effect.IOApp
import cats.effect.unsafe.IORuntime
import com.datastax.oss.driver.api.core.cql.BatchType
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Instant, LocalDate, ZoneOffset}
import java.util.UUID
import scala.concurrent.duration.*

class IntegrationSpec extends AnyFreeSpec with Matchers with CassandraAware:

  implicit val ioRuntime: IORuntime = IORuntime.global

  case class Metadata(createdAt: Instant, updatedAt: Option[Instant], author: String)

  object Metadata:
    implicit val jsonCodec: io.circe.Codec[Metadata] = io.circe.generic.semiauto.deriveCodec

  object events extends Table[
    "events",
    (
      "id" :=: uuid,
      "start_time" :=: timestamp,
      "artists" :=: list[varchar],
      "venue" :=: nullable[text],
      "prices" :=: map[varchar, decimal],
      "tags" :=: set[varchar],
      "metadata" :=: json[Metadata]
    )
  ]

  case class Event(
    id: UUID,
    startTime: Instant,
    artists: List[String],
    venue: Option[String],
    prices: Map[String, BigDecimal],
    tags: Set[String],
    metadata: Metadata
  )

  val event1 = Event(
    UUID.randomUUID(),
    LocalDate.now.atStartOfDay.toInstant(ZoneOffset.UTC),
    List("Radiohead", "Sigur Ros"),
    Some("Roundhouse"),
    Map(
      "USD" -> BigDecimal(20.5),
      "GBP" -> BigDecimal(16)
    ),
    Set("rock", "post rock", "indie"),
    Metadata(Instant.now, None, "epifab")
  )

  val event2 = event1.copy(
    id = UUID.randomUUID(),
    startTime = LocalDate.now.plusDays(1).atStartOfDay.toInstant(ZoneOffset.UTC)
  )

  val insert: Command[Event] =
    Insert
      .into(events)
      .usingTtl(15.seconds)
      .compile
      .pcontramap[Event]

  val select: Query[Unit, Event] =
    Select
      .from(events)
      .take(_.*)
      .compile
      .pmap[Event]

  "Insert CQL" in {
    insert.cql shouldBe "INSERT INTO events" +
      " (id, start_time, artists, venue, prices, tags, metadata)" +
      " VALUES (?, ?, ?, ?, ?, ?, ?)" +
      " USING TTL 15"
  }

  "Select CQL" in {
    select.cql shouldBe "SELECT id, start_time, artists, venue, prices, tags, metadata FROM events"
  }

  "Can insert / retrieve data" in {
    cassandraRuntime.use(cassandra =>
      for {
        _ <- cassandra.execute("TRUNCATE events", List.empty)
        _ <- cassandra.executeBatch(insert, BatchType.LOGGED)(List(event1, event2))
        result <- cassandra.execute(select)(()).compile.toList
      } yield result
    ).unsafeRunSync().toSet shouldBe Set(event1, event2)

  }
