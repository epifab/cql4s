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

  val cassandraRuntime = CassandraZIORuntime(CassandraTestConfig)
  val ioRuntime = zio.Runtime.default

  "Can insert / retrieve data" in {
    val program: ZIO[Has[CqlSession], Throwable, (Option[Event], Option[Event])] = for {
      _ <- CassandraZIORuntime.execute(truncateEvents)(())
      _ <- CassandraZIORuntime.executeBatch(insertEvent, BatchType.LOGGED)(List(event1, event2))
      _ <- CassandraZIORuntime.stream(findEventById)(event1.id).tap(e => CassandraZIORuntime.execute(updateEventTickets)((
        Map(Currency.getInstance("USD") -> 32),
        e.metadata.copy(updatedAt = Some(now)),
        e.id
      ))).run(ZSink.drain)
      updatedEvent <- CassandraZIORuntime.stream(findEventById)(event1.id).run(ZSink.last[Event])
      _ <- CassandraZIORuntime.execute(deleteEvent)(event1.id)
      deletedEvent <- CassandraZIORuntime.stream(findEventById)(event1.id).run(ZSink.last[Event])
    } yield (updatedEvent, deletedEvent)

    ioRuntime.unsafeRun(program.provideLayer(cassandraRuntime)) shouldBe (
      Some(event1.copy(
        tickets = Map(Currency.getInstance("USD") -> 32),
        metadata = event1.metadata.copy(updatedAt = Some(now))
      )),
      None
    )
  }
