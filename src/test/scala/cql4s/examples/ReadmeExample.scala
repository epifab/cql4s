import cats.effect.{ExitCode, IO, IOApp}
import cql4s.*

import java.time.Instant
import java.util.{Currency, UUID}
import scala.util.chaining.*


// The model
case class User(name: String, email: Option[String])

case class Metadata(createdAt: Instant, updatedAt: Option[Instant], author: User)

case class Event(
  id: UUID,
  startTime: Instant,
  artists: List[String],
  venue: String,
  tickets: Map[Currency, BigDecimal],
  tags: Set[String],
  metadata: Metadata
)

// Build a new type based on a primitive type
trait currencyType

object currencyType:
  given DataTypeCodec[currencyType, String, Currency] =
    DataType.textCodec.map(_.getCurrencyCode, Currency.getInstance)


// User defined types

class userType extends udt[
  User,
  "music",  // keyspace
  "user",   // udt name
  (
    "name" :=: text,
    "email" :=: nullable[text]
  )
]

class metadataType extends udt[
  Metadata,
  "music",    // keyspace
  "metadata", // udt name
  (
    "createdAt" :=: timestamp,
    "updatedAt" :=: nullable[timestamp],
    "author" :=: userType   // nested udt
  )
]

object events extends Table[
  "music",  // keyspace
  "events", // table name
  (
    "id" :=: uuid,
    "start_time" :=: timestamp,
    "artists" :=: list[varchar],
    "venue" :=: text,
    "tickets" :=: map[currencyType, decimal],
    "tags" :=: set[varchar],
    "metadata" :=: metadataType
  )
]

class EventsRepo[F[_]](using cassandra: CassandraRuntime[F]):
  val add: Event => F[Unit] =
    Insert
      .into(events)
      .compile
      .pcontramap[Event]
      .run

  val updateTickets: (Map[Currency, BigDecimal], Metadata, UUID) => F[Unit] =
    Update(events)
      .set(e => (e("tickets"), e("metadata")))
      .where(_("id") === :?)
      .compile
      .run
      .pipe(Function.untupled)

  val findById: UUID => fs2.Stream[F, Event] =
    Select
      .from(events)
      .take(_.*)
      .where(_("id") === :?)
      .compile
      .pmap[Event]
      .run


object Program extends IOApp:
  val cassandraConfig = CassandraConfig(
    "0.0.0.0",
    9042,
    credentials = None,
    keyspace = None,
    datacenter = "testdc"
  )

  def run(args: List[String]): IO[ExitCode] =
    CassandraRuntime[IO](cassandraConfig).map(cassandra => new EventsRepo(using cassandra)).use { repo =>
      repo
        .findById(UUID.fromString("246BDDC4-BAF3-41BF-AFB5-FA0992E4DC6B"))
        // Update existing event price
        .evalTap(e => repo.updateTickets(
          Map(Currency.getInstance("GBP") -> 49.99),
          e.metadata.copy(updatedAt = Some(Instant.now)),
          e.id
        ))
        // Create a new event with same artists on a different day
        .evalTap(e => repo.add(e.copy(
          id = UUID.randomUUID(),
          startTime = Instant.parse("2022-03-08T20:30:00Z"),
          metadata = e.metadata.copy(createdAt = Instant.now, updatedAt = None)
        )))
        .compile
        .drain
        .as(ExitCode.Success)
    }