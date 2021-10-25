package cql4s.examples.readme

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
