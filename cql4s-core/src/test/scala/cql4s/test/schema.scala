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
