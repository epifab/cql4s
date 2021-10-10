package cql4s

import cats.data.Chain.Singleton
import cats.effect.IOApp
import cats.effect.unsafe.IORuntime
import com.datastax.oss.driver.api.core.cql.BatchType
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Instant, LocalDate, ZoneOffset}
import java.util.UUID

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

  val insert: Command[Event] =
    Insert
      .into(events)
      .compile
      .pcontramap[Event]

  val select: Query[Unit, Event] =
    Select
      .from(events)
      .take(_.*)
      .compile
      .pmap[Event]

  "Can insert / retrieve data" in {

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

    cassandraRuntime.use(cassandra =>
      for {
        _ <- cassandra.execute("TRUNCATE events", List.empty)
        _ <- cassandra.executeBatch(insert, BatchType.LOGGED)(List(event1, event2))
        result <- cassandra.execute(select)(()).compile.toList
      } yield result
    ).unsafeRunSync() shouldBe List(event1, event2)

  }
