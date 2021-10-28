package cql4s

import cats.effect.kernel.Resource
import cats.effect.unsafe.IORuntime
import cats.effect.IO
import com.datastax.oss.driver.api.core.cql.BatchType
import cql4s.dsl.*
import cql4s.test.CassandraTestConfig
import cql4s.test.fixtures.*
import cql4s.test.model.*
import cql4s.test.queries.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.Currency

class IntegrationCatsSpec extends AnyFreeSpec with Matchers with CassandraAware:

  "Can insert / retrieve data" in {
    val result: IO[(Event, Option[Event])] =
      for {
        given _ <- cassandra
        _ <- truncateEvents.execute(())
        _ <- insertEvent.executeBatch(BatchType.LOGGED)(List(event1, event2))
        _ <- findEventsById.stream(List(event1.id, event2.id)).evalTap(e => updateEventTickets.execute((
          Map(Currency.getInstance("USD") -> 32),
          e.metadata.copy(updatedAt = Some(now)),
          e.id
        ))).compile.drain
        updatedEvent <- findEventById.one(event1.id)
        _ <- deleteEvent.execute(event1.id)
        deletedEvent <- findEventById.option(event1.id)
      } yield (updatedEvent, deletedEvent)

    result.unsafeRunSync() shouldBe (
      event1.copy(
        tickets = Map(Currency.getInstance("USD") -> 32),
        metadata = event1.metadata.copy(updatedAt = Some(now))
      ),
      None
    )
  }
