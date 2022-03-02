# CQL4s

[![Build Status](https://app.travis-ci.com/epifab/cql4s.svg?branch=main)](https://app.travis-ci.com/epifab/cql4s)

CQL4s is a typesafe CQL DSL and Cassandra client for *Scala 3*.


## Why CQL4s

CQL4s brings Cassandra to the wonderful world of functional Scala,
by supporting out of the box [cats effect](https://typelevel.org/cats-effect) / [fs2](https://fs2.io/)
and [ZIO 2](https://zio.dev/).

CQL4s is a pure functional layer built on top of the [Datastax Java driver](https://github.com/datastax/java-driver).  
The main difference with similar libraries lies on a strongly typed, "as close as possible to cql" DSL,
that will help to prevent common mistakes **at compile time**, such as 
referring to a non-existing table, type or column,
comparing fields of unrelated types, 
decoding the results of a query into an incompatible data structure
and issues related to placeholders encoding.

CQL4s was strongly inspired by [Tydal](https://github.com/epifab/tydal3).



## Usage example

#### The model

Simple, dependency-free model for the data stored in Cassandra.

```scala
import java.time.Instant
import java.util.{Currency, UUID}

case class User(name: String, email: Option[String], phone: (Short, String))

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
import cql4s.dsl.*

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
    "name"  :=: text,
    "email" :=: nullable[text],
    "phone" :=: (smallint, text)  // tuple
  )
]

class metadataType extends udt[
  Metadata,   // model case class
  "music",    // keyspace
  "metadata", // udt name
  (
    "createdAt" :=: timestamp,
    "updatedAt" :=: nullable[timestamp],
    "author"    :=: userType   // nested udt
  )
]

// ------
// Tables
// ------

object events extends Table[
  "music",  // keyspace
  "events", // table name
  (
    "id"         :=: uuid,
    "start_time" :=: timestamp,
    "artists"    :=: list[text],
    "venue"      :=: text,
    "tickets"    :=: map[currencyType, decimal],
    "tags"       :=: set[text],
    "metadata"   :=: metadataType
  )
]
```

#### Queries and commands

Typically, you might want to extract away all your queries and commands.

```scala
import cql4s.CassandraRuntimeAlgebra
import cql4s.dsl.*

import java.util.{Currency, UUID}
import scala.util.chaining.*

class EventsRepo[F[_], S[_]](using CassandraRuntimeAlgebra[F, S]):
  val add: Event => F[Unit] =
    Insert
      .into(events)
      .compile
      .pcontramap[Event]
      .execute

  val updateTickets: (Map[Currency, BigDecimal], Metadata, UUID) => F[Unit] =
    Update(events)
      .set(e => (e("tickets"), e("metadata")))
      .where(_("id") === :?)
      .compile
      .execute
      .pipe(Function.untupled)

  val findByIds: List[UUID] => S[Event] =
    Select
      .from(events)
      .take(_.*)
      .where(_("id") in :?)
      .compile
      .pmap[Event]
      .stream
```

#### The app (typelevel example)

Here's what the application might look like in cats effect/fs2 land:

```scala
import cats.effect.{ExitCode, IO, IOApp}
import cql4s.{CassandraCatsRuntime, CassandraConfig}

import java.time.Instant
import java.util.{Currency, UUID}

object Program extends IOApp:
  val cassandraConfig = CassandraConfig(
    "0.0.0.0",
    9042,
    credentials = None,
    keyspace = None,
    datacenter = "testdc"
  )
  
  def run(args: List[String]): IO[ExitCode] =
    CassandraCatsRuntime[IO](cassandraConfig)
      .map(cassandra => new EventsRepo(using cassandra))
      .use { repo =>
        repo
          .findByIds(args.map(UUID.fromString))
          // Update existing event tickets
          .evalTap(e => repo.updateTickets(
            Map(Currency.getInstance("GBP") -> 49.99),
            e.metadata.copy(updatedAt = Some(Instant.now)),
            e.id
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

#### The app (ZIO example)

Here's what the application might look like in ZIO land:

```scala
import cql4s.{CassandraConfig, CassandraZIORuntime, CassandraZLayer}
import zio.{ZIO, ZIOAppArgs, ZIOAppDefault}

import java.time.Instant
import java.util.{Currency, UUID}

object Program extends ZIOAppDefault:
  val cassandraLayer = CassandraZLayer(CassandraConfig(
    "0.0.0.0",
    9042,
    credentials = None,
    keyspace = None,
    datacenter = "testdc"
  ))

  val app = for {
    args <- ZIO.service[ZIOAppArgs]
    repo = new EventsRepo(using CassandraZIORuntime)
    result <- repo
      .findByIds(args.getArgs.toList.map(UUID.fromString))
      // Update existing event price
      .tap(e => repo.updateTickets(
        Map(Currency.getInstance("GBP") -> 49.99),
        e.metadata.copy(updatedAt = Some(Instant.now)),
        e.id
      ))
      // Create a new event with same artists on a different day
      .tap(e => repo.add(
        e.copy(
          id = UUID.randomUUID(),
          startTime = Instant.parse("2022-03-08T20:30:00Z"),
          metadata = e.metadata.copy(createdAt = Instant.now, updatedAt = None)
        )
      ))
      .runDrain
  } yield ()
  
  val run = app.provideSomeLayer(cassandraLayer)
```

## Support

Find out all supported feature [here](SUPPORT.md).


## Getting started

### Installation (sbt)

#### Step 1. Add the JitPack repository to your build file

    resolvers += "jitpack" at "https://jitpack.io"


#### Step 2. Add the dependencies

For the typelevel stack:

```scala
libraryDependencies ++= Seq(
    "com.github.epifab.cql4s" %% "cql4s-core",
    "com.github.epifab.cql4s" %% "cql4s-cats"
).map(_ % Version)
```

For ZIO:

```scala
libraryDependencies ++= Seq(
    "com.github.epifab.cql4s" %% "cql4s-core",
    "com.github.epifab.cql4s" %% "cql4s-zio"
).map(_ % Version)
```

Optional: JSON support using circe

```scala
libraryDependencies += "com.github.epifab.cql4s" %% "cql4s-circe" % Version
```


## Testing

```shell
$ docker-compose up -d
$ sbt test
```

