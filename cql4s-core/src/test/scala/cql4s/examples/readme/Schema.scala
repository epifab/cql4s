package cql4s.examples.readme

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
    "name" :=: text,
    "email" :=: nullable[text],
    "phone" :=: (smallint, text)
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
