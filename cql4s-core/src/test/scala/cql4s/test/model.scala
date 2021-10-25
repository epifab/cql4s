package cql4s.test

import java.time.Instant
import java.util.{Currency, UUID}

object model:
  case class User(name: String, email: Option[String], phone: (Short, String))

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
