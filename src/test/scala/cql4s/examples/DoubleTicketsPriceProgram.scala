package cql4s.examples

import cats.effect.{ExitCode, IO, IOApp}
import cql4s.*
import cql4s.keyspaces.Music.{Event, Metadata, events}

import java.time.Instant
import java.util.{Currency, UUID}
import scala.concurrent.duration.*

trait EventRepo:
  def findEventsByVenue(venue: String): fs2.Stream[IO, Event]
  def addEvent(event: Event): IO[Unit]
  def updateEventPrice(eventId: UUID, prices: Map[Currency, BigDecimal]): IO[Unit]


val GBP = Currency.getInstance("GBP")
val USD = Currency.getInstance("USD")


object EventRepo:
  private val insert: Command[Event] =
    Insert
      .into(events)
      .compile
      .pcontramap[Event]

  private val select: Query[String, Event] =
    Select
      .from(events)
      .take(_.*)
      .where(_("venue") === :?)
      .allowFiltering(true)
      .compile
      .pmap[Event]

  private val update: Command[(Map[Currency, BigDecimal], UUID)] =
    Update(events)
      .set(_("prices"))
      .where(_("id") === :?)
      .compile

  def apply(cassandra: CassandraRuntime): EventRepo = new EventRepo:
    override def findEventsByVenue(venue: String): fs2.Stream[IO, Event] = cassandra.execute(select)(venue)
    override def addEvent(event: Event): IO[Unit] = cassandra.execute(insert)(event)
    override def updateEventPrice(id: UUID, prices: Map[Currency, BigDecimal]): IO[Unit] = cassandra.execute(update)((prices, id))


object DoubleTicketsPriceProgram extends IOApp:
  val cassandraConfig = CassandraConfig(
    "0.0.0.0",
    9042,
    credentials = None,
    keyspace = None,
    datacenter = "testdc"
  )

  def run(args: List[String]): IO[ExitCode] =
    def event(date: String, venue: String) = {
      Event(
        id = UUID.randomUUID(),
        startTime = Instant.parse(date),
        artists = List("Radiohead", "Sigur Ros"),
        venue = venue,
        prices = Map(GBP -> 49.99, USD -> 79.99),
        tags = Set("rock", "post rock", "indie"),
        metadata = Metadata(
          createdAt = Instant.now,
          updatedAt = None,
          author = "epifab"
        )
      )
    }

    CassandraRuntime(cassandraConfig).map(EventRepo.apply)
      .use { repo =>
        (for {
          // Create event
          _ <- fs2.Stream.eval(repo.addEvent(event("2018-03-01T20:30:00Z", "Roundhouse")))
          _ <- fs2.Stream.eval(repo.addEvent(event("2018-03-02T20:30:00Z", "Roundhouse")))
          _ <- fs2.Stream.eval(repo.addEvent(event("2018-03-03T20:30:00Z", "O2 Academy")))
          // Every 10 seconds inflate the price for all events at the Roundhouse
          _ <- fs2.Stream.awakeEvery[IO](10.seconds)
          _ <- repo.findEventsByVenue("Roundhouse")
            .evalTap(e => IO(println("About to double the price for: ")) *> IO(println(e)))
            .evalTap(e => repo.updateEventPrice(e.id, e.prices.map { case (currency, price) => currency -> price * 2 }))
        } yield ()).compile.drain
      }
      .as(ExitCode.Success)
