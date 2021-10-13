package cql4s
package keyspaces

import java.time.Instant
import java.util.{Currency, UUID}


object Music:
  trait currency

  object currency:
    given currencyDataType: DataTypeCodec[currency, String, Currency] =
      DataType.textCodec.map[currency, Currency](_.getCurrencyCode, Currency.getInstance)

  case class Metadata(createdAt: Instant, updatedAt: Option[Instant], author: String)

  object Metadata:
    implicit val jsonCodec: io.circe.Codec[Metadata] = io.circe.generic.semiauto.deriveCodec

  case class Event(
    id: UUID,
    venue: String,
    startTime: Instant,
    artists: List[String],
    prices: Map[Currency, BigDecimal],
    tags: Set[String],
    metadata: Metadata
  )

  object events extends Table[
    "music",
    "events",
    (
      "id" :=: uuid,
      "venue" :=: text,
      "start_time" :=: timestamp,
      "artists" :=: list[varchar],
      "prices" :=: map[currency, decimal],
      "tags" :=: set[varchar],
      "metadata" :=: json[Metadata]
    )
  ]
