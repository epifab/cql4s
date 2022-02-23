package cql4s.test

import cql4s.dsl.*
import cql4s.test.model.*

import java.time.Instant
import java.util.{Currency, UUID}

object schema:
  trait currency

  object currency:
    given currencyDataType: DataTypeCodec[currency, String, Currency] =
      DataType.textCodec.map[currency, Currency](_.getCurrencyCode, Currency.getInstance)

  class userType extends udt[
    User,
    "music",  // keyspace
    "user",   // udt name
    (
      "name" :=: text,
      "email" :=: nullable[text],
      "phone" :=: (smallint, text)
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

  object all_types extends Table[
    "test",
    "all_types",
    (
      "id" :=: uuid,
      "_ascii" :=: ascii,
      "_bigint" :=: bigint,
      "_blob" :=: blob,
      "_boolean" :=: boolean,
      // cannot mix counter and non-counter column ¯\_(ツ)_/¯
      // "_counter" :=: counter,
      "_date" :=: date,
      "_decimal" :=: decimal,
      "_double" :=: double,
      // duration is not supported
      // "_duration" :=: duration,
      "_float" :=: float,
      "_inet" :=: inet,
      "_int" :=: int,
      "_smallint" :=: smallint,
      "_text" :=: text,
      "_time" :=: time,
      "_timestamp" :=: timestamp,
      "_timeuuid" :=: timeuuid,
      "_tinyint" :=: tinyint,
      "_uuid" :=: uuid,
      "_varchar" :=: varchar,
      "_varint" :=: varint,
      "_nullableint" :=: nullable[int]
    )
  ]
