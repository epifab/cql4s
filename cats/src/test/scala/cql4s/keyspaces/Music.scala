package cql4s.keyspaces

import cql4s.dsl.*

import java.time.Instant
import java.util.{Currency, UUID}


object Music:
  trait currency

  object currency:
    given currencyDataType: DataTypeCodec[currency, String, Currency] =
      DataType.textCodec.map[currency, Currency](_.getCurrencyCode, Currency.getInstance)

  case class User(name: String, email: Option[String])
  case class Metadata(createdAt: Instant, updatedAt: Option[Instant], author: User)

  case class Event(
    id: UUID,
    venue: String,
    startTime: Instant,
    artists: List[String],
    tickets: Map[Currency, BigDecimal],
    tags: Set[String],
    metadata: Metadata
  )

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
    "music",
    "events",
    (
      "id" :=: uuid,
      "venue" :=: text,
      "start_time" :=: timestamp,
      "artists" :=: list[varchar],
      "tickets" :=: map[currency, decimal],
      "tags" :=: set[varchar],
      "metadata" :=: metadataType
    )
  ]
