package cql4s.examples

import cats.effect.{ExitCode, IO, IOApp}
import cql4s.*

import java.time.Instant
import java.util.UUID
import scala.concurrent.duration.*

object events extends Table[
  "events",
  (
    "id" :=: uuid,
    "venue" :=: text,
    "start_time" :=: timestamp,
    "artists" :=: list[varchar],
    "prices" :=: map[varchar, decimal],
    "tags" :=: set[varchar]
  )
]

case class Event(
  id: UUID,
  venue: String,
  startTime: Instant,
  artists: List[String],
  prices: Map[String, BigDecimal],
  tags: Set[String]
)

object DoubleTicketsPriceProgram extends IOApp:
  val cassandraConfig = CassandraConfig(
    "0.0.0.0",
    9042,
    credentials = None,
    keyspace = "music",
    datacenter = "testdc"
  )

  val insert: Command[Event] =
    Insert
      .into(events)
      .compile
      .pcontramap[Event]

  val select: Query[String, Event] =
    Select
      .from(events)
      .take(_.*)
      .where(_("venue") === :?)
      .allowFiltering(true)
      .compile
      .pmap[Event]

  val update: Command[(Map[String, BigDecimal], UUID)] =
    Update(events)
      .set(_("prices"))
      .where(_("id") === :?)
      .compile

  def run(args: List[String]): IO[ExitCode] =
    def event(date: String, venue: String) = Event(
      id = UUID.randomUUID(),
      startTime = Instant.parse(date),
      artists = List("Radiohead", "Sigur Ros"),
      venue = venue,
      prices = Map("GBP" -> 49.99, "USD" -> 79.99),
      tags = Set("rock", "post rock", "indie")
    )

    CassandraRuntime(cassandraConfig)
      .use { cassandra =>
        (for {
          // Create event
          _ <- fs2.Stream.eval(cassandra.execute(insert)(event("2018-03-01T20:30:00Z", "Roundhouse")))
          _ <- fs2.Stream.eval(cassandra.execute(insert)(event("2018-03-02T20:30:00Z", "Roundhouse")))
          _ <- fs2.Stream.eval(cassandra.execute(insert)(event("2018-03-03T20:30:00Z", "O2 Academy")))
          // Every 10 seconds inflate the price for all events at the Roundhouse
          _ <- fs2.Stream.awakeEvery[IO](10.seconds)
          _ <- cassandra.execute(select)("Roundhouse")
            .evalTap(e => IO(println("About to double the price for: ")) *> IO(println(e)))
            .map(e => (e.prices.map { case (currency, price) => currency -> price * 2 }, e.id))
            .evalTap(cassandra.execute(update))
        } yield ()).compile.drain
      }
      .as(ExitCode.Success)
