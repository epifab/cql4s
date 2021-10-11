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



## Canonical example

```scala
import cql4s.*
import cats.effect.{ExitCode, IO, IOApp}

import java.time.Instant
import java.util.UUID

object events extends Table[
  "events",
  (
    "id" :=: uuid,
    "start_time" :=: timestamp,
    "artists" :=: list[varchar],
    "venue" :=: nullable[text],
    "prices" :=: map[varchar, decimal],
    "tags" :=: set[varchar]
  )
]

case class Event(
  id: UUID,
  date: Instant,
  artists: List[String],
  venue: Option[String],
  prices: Map[String, BigDecimal],
  tags: Set[String]
)

object Program extends IOApp:
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

  val select: Query[UUID, Event] =
    Select
      .from(events)
      .take(_.*)
      .where(_("id") === :?)
      .compile
      .pmap[Event]

  def run(args: List[String]): IO[ExitCode] =
    CassandraRuntime(cassandraConfig)
      .use { cassandra =>
        cassandra.execute(select)(UUID.fromString("246BDDC4-BAF3-41BF-AFB5-FA0992E4DC6B"))
          .map(_.copy(prices = Map("GBP" -> BigDecimal(49.99))))
          .evalTap(cassandra.execute(insert))
          .compile
          .drain
      }
      .as(ExitCode.Success)
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
