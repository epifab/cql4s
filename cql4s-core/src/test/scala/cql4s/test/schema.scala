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

  object dummy extends Table[
    "test",
    "dummy",
    (
      "id" :=: uuid,
      "_ascii" :=: nullable[ascii],
      "_bigint" :=: nullable[bigint],
      "_blob" :=: nullable[blob],
      "_boolean" :=: nullable[boolean],
      // cannot mix counter and non-counter column ¯\_(ツ)_/¯
      // "_counter" :=: counter,
      "_date" :=: nullable[date],
      "_decimal" :=: nullable[decimal],
      "_double" :=: nullable[double],
      // duration is not supported
      // "_duration" :=: nullable[duration],
      "_float" :=: nullable[float],
      "_inet" :=: nullable[inet],
      "_int" :=: nullable[int],
      "_smallint" :=: nullable[smallint],
      "_text" :=: nullable[text],
      "_time" :=: nullable[time],
      "_timestamp" :=: nullable[timestamp],
      "_timeuuid" :=: nullable[timeuuid],
      "_tinyint" :=: nullable[tinyint],
      "_uuid" :=: nullable[uuid],
      "_varchar" :=: nullable[varchar],
      "_varint" :=: nullable[varint]
    )
  ]

  object plusone extends udf["test", "plusone", int, int]
