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
    
  val update: Command[(Map[String, BigDecimal], Metadata, UUID)] =
    Update(events)
      .set(e => (e("prices"), e("metadata")))
      .where(_("id") === :?)
      .compile

  val select: Query[UUID, Event] =
    Select
      .from(events)
      .take(_.*)
      .where(_("id") === :?)
      .compile
      .pmap[Event]

  "Insert CQL" in {
    insert.cql shouldBe "INSERT INTO events" +
      " (id, start_time, artists, venue, prices, tags, metadata)" +
      " VALUES (?, ?, ?, ?, ?, ?, ?)" +
      " USING TTL 15"
  }

  "Update CQL" in {
    update.cql shouldBe "UPDATE events" +
      " SET prices = ?, metadata = ?" +
      " WHERE id = ?"
  }

  "Select CQL" in {
    select.cql shouldBe "SELECT id, start_time, artists, venue, prices, tags, metadata FROM events WHERE id = ?"
  }

  "Can insert / retrieve data" in {
    val now = Instant.now
    cassandraRuntime.use(cassandra =>
      for {
        _ <- cassandra.execute("TRUNCATE events", List.empty)
        _ <- cassandra.executeBatch(insert, BatchType.LOGGED)(List(event1, event2))
        _ <- cassandra.execute(select)(event1.id).evalTap(e => cassandra.execute(update)((
          Map("USD" -> 32),
          e.metadata.copy(updatedAt = Some(now)),
          e.id
        ))).compile.drain
        updatedEvents <- cassandra.execute(select)(event1.id).compile.toList
      } yield updatedEvents
    ).unsafeRunSync().toSet shouldBe Set(event1.copy(prices = Map("USD" -> 32), metadata = event1.metadata.copy(updatedAt = Some(now))))
  }
