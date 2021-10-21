package cql4s.test

import cql4s.dsl.*
import cql4s.test.model.*
import cql4s.test.schema.*

import java.util.{Currency, UUID}
import scala.concurrent.duration.DurationInt

object queries:

  val truncateEvents: Command[Unit] =
    Truncate(events).compile

  val insertEvent: Command[Event] =
    Insert
      .into(events)
      .usingTtl(15.seconds)
      .compile
      .pcontramap[Event]

  val updateEventTickets: Command[(Map[Currency, BigDecimal], Metadata, UUID)] =
    Update(events)
      .set(e => (e("tickets"), e("metadata")))
      .where(_("id") === :?)
      .compile

  val deleteEvent: Command[UUID] =
    Delete
      .from(events)
      .where(_("id") === :?)
      .compile

  val findEventById: Query[UUID, Event] =
    Select
      .from(events)
      .take(_.*)
      .where(_("id") === :?)
      .compile
      .pmap[Event]
