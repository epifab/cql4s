package cql4s.examples

import cats.effect.{ExitCode, IO, IOApp}
import cql4s.*
import cql4s.keyspaces.Music.{Event, Metadata, User, events}

import java.time.Instant
import java.util.{Currency, UUID}
import scala.concurrent.duration.*

trait EventRepo[F[_]]:
  def findEventsByVenue(venue: String): fs2.Stream[F, Event]
  def addEvent(event: Event): F[Unit]
  def updateEventPrice(eventId: UUID, tickets: Map[Currency, BigDecimal]): F[Unit]


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
      .set(_("tickets"))
      .where(_("id") === :?)
      .compile

  def apply[F[_]](cassandra: CassandraRuntime[F]): EventRepo[F] = new EventRepo:
    override def findEventsByVenue(venue: String): fs2.Stream[F, Event] =
      cassandra.execute(select)(venue)

    override def addEvent(event: Event): F[Unit] =
      cassandra.execute(insert)(event)

    override def updateEventPrice(id: UUID, tickets: Map[Currency, BigDecimal]): F[Unit] =
      cassandra.execute(update)((tickets, id))


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
        tickets = Map(GBP -> 49.99, USD -> 79.99),
        tags = Set("rock", "post rock", "indie"),
        metadata = Metadata(
          createdAt = Instant.now,
          updatedAt = None,
          author = User("epifab", Some("info@epifab.solutions"))
        )
      )
    }

    CassandraRuntime[IO](cassandraConfig).map(EventRepo.apply)
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
            .evalTap(e => repo.updateEventPrice(e.id, e.tickets.map { case (currency, price) => currency -> price * 2 }))
        } yield ()).compile.drain
      }
      .as(ExitCode.Success)
