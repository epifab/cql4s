# CQL4s

[![Build Status](https://app.travis-ci.com/epifab/cql4s.svg?branch=main)](https://app.travis-ci.com/epifab/cql4s)

CQL4s is a typesafe CQL (Cassandra Query Language) DSL for *Scala 3*, cats effect and fs2 
built on top of the [Datastax Java driver](https://github.com/datastax/java-driver).


## Why CQL4s

CQL4s brings Cassandra to the wonderful world of functional Scala through cats effect and fs2.

Thanks to its strongly typed DSL, it also helps to prevent common mistakes **at compile time**, such as 
typos (referring to a non-existing table or column),
comparing fields of unrelated types, 
decoding the results of a query into an incompatible data structure,
issues with placeholder encoding for queries and commands.

CQL4s was strongly inspired by [Tydal](https://github.com/epifab/tydal3).



## Usage example

#### The model

Simple, dependency-free model for the data stored in Cassandra.

```scala
import java.time.Instant
import java.util.{Currency, UUID}

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
```

#### The schema

The schema includes all tables, user defined types and custom types modelled on top of existing ones.

```scala
import cql4s.*

import java.util.Currency

// ------------
// Custom types
// ------------

trait currencyType

object currencyType:
  given DataTypeCodec[currencyType, String, Currency] = 
    DataType.textCodec.map(_.getCurrencyCode, Currency.getInstance)


// ------------------
// User defined types
// ------------------

class userType extends udt[
  User,     // model case class
  "music",  // keyspace
  "user",   // udt name
  (
    "name" :=: text,
    "email" :=: nullable[text]
  )
]

class metadataType extends udt[
  Metadata,   // model case class
  "music",    // keyspace
  "metadata", // udt name
  (
    "createdAt" :=: timestamp,
    "updatedAt" :=: nullable[timestamp],
    "author" :=: userType   // nested udt
  )
]

// ------
// Tables
// ------

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
```

#### Queries and commands

Typically, you might want to extract away all your queries and commands.

```scala
import cql4s.*
import fs2.Stream

import java.util.{Currency, UUID}

class EventsRepo[F[_]](cassandra: CassandraRuntime[F]):
  private val insert: Command[Event] =
    Insert
      .into(events)
      .compile
      .pcontramap[Event]

  private val update: Command[(Map[Currency, BigDecimal], Metadata, UUID)] =
    Update(events)
      .set(e => (e("tickets"), e("metadata")))
      .where(_("id") === :?)
      .compile

  private val select: Query[UUID, Event] =
    Select
      .from(events)
      .take(_.*)
      .where(_("id") === :?)
      .compile
      .pmap[Event]
  
  def findById(id: UUID): Stream[F, Event] =
    cassandra.execute(select)(id)
  
  def add(event: Event): F[Unit] =
    cassandra.execute(insert)(event)
  
  def updateTickets(id: UUID, tickets: Map[Currency, BigDecimal], metadata: Metadata): F[Unit] =
    cassandra.execute(update)((tickets, metadata, id))
```

#### The app

```scala
import cats.effect.{ExitCode, IO, IOApp}
import cql4s.{CassandraConfig, CassandraRuntime}

object Program extends IOApp:
  val cassandraConfig = CassandraConfig(
    "0.0.0.0",
    9042,
    credentials = None,
    keyspace = None,
    datacenter = "testdc"
  )

  def run(args: List[String]): IO[ExitCode] =
    CassandraRuntime[IO](cassandraConfig).map(new EventsRepo(_)).use { repo =>
      repo
        .findById(UUID.fromString("246BDDC4-BAF3-41BF-AFB5-FA0992E4DC6B"))
        // Update existing event tickets
        .evalTap(e => repo.updateTickets(
          id = e.id,
          tickets = Map(Currency.getInstance("GBP") -> 49.99),
          metadata = e.metadata.copy(updatedAt = Some(Instant.now))
        )) 
        // Create a new event with same artists on a different day
        .evalTap(e => repo.add(e.copy(
          id = UUID.randomUuid(),
          startTime = Instant.parse("2022-03-08T20:30:00Z"), 
          metadata = e.metadata.copy(createdAt = Instant.now, updatedAt = None)
        )))
        .compile
        .drain
        .as(ExitCode.Success)
    }
```

## Support

Find out all supported feature [here](SUPPORT.md).


## Testing

```shell
$ docker-compose up -d
$ sbt test
```


## Getting started

### Installation (sbt)

Coming soon
