package cql4s

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.BatchType
import cql4s.dsl.*
import cql4s.test.CassandraTestConfig
import cql4s.test.fixtures.*
import cql4s.test.model.*
import cql4s.test.queries.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import zio.{Has, ZIO, ZLayer}
import zio.stream.ZSink

import java.util.Currency

class IntegrationZIOSpec extends AnyFreeSpec with Matchers:

  "Can insert / retrieve data" in {
    val program: ZIO[Has[CqlSession], Throwable, (Event, Option[Event])] = for {
      _ <- truncateEvents.execute(())
      _ <- insertEvent.executeBatch(BatchType.LOGGED)(List(event1, event2))
      _ <- findEventsById.stream(List(event1.id, event2.id)).tap(e => updateEventTickets.execute((
        Map(Currency.getInstance("USD") -> 32),
        e.metadata.copy(updatedAt = Some(now)),
        e.id
      ))).runDrain
      updatedEvent <- findEventById.one(event1.id)
      _ <- deleteEvent.execute(event1.id)
      deletedEvent <- findEventById.option(event1.id)
    } yield (updatedEvent, deletedEvent)

    zio.Runtime.default.unsafeRun(program.provideLayer(CassandraZLayer(CassandraTestConfig))) shouldBe (
      event1.copy(
        tickets = Map(Currency.getInstance("USD") -> 32),
        metadata = event1.metadata.copy(updatedAt = Some(now))
      ),
      None
    )
  }
