package cql4s

import com.datastax.oss.driver.api.core.cql.BatchType
import cql4s.dsl.*
import cql4s.test.CassandraTestConfig
import cql4s.test.fixtures.*
import cql4s.test.model.*
import cql4s.test.queries.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import zio.ZIO
import zio.stream.ZSink

import java.util.Currency

class IntegrationZioSpec extends AnyFreeSpec with Matchers:

  val cassandraRuntime: ZIO.Release[Any, Throwable, CassandraZioRuntime] = CassandraZioRuntime(CassandraTestConfig)
  val ioRuntime = zio.Runtime.default

  "Can insert / retrieve data" in {
    val result: ZIO[Any, Throwable, (Option[Event], Option[Event])] = cassandraRuntime(cassandra =>
      for {
        _ <- cassandra.execute(truncateEvents)(())
        _ <- cassandra.executeBatch(insertEvent, BatchType.LOGGED)(List(event1, event2))
        _ <- cassandra.execute(findEventById)(event1.id).tap(e => cassandra.execute(updateEventTickets)((
          Map(Currency.getInstance("USD") -> 32),
          e.metadata.copy(updatedAt = Some(now)),
          e.id
        ))).run(ZSink.drain)
        updatedEvent <- cassandra.execute(findEventById)(event1.id).run(ZSink.last[Event])
        _ <- cassandra.execute(deleteEvent)(event1.id)
        deletedEvent <- cassandra.execute(findEventById)(event1.id).run(ZSink.last[Event])
      } yield (updatedEvent, deletedEvent)
    )

    ioRuntime.unsafeRun(result) shouldBe (
      Some(event1.copy(
        tickets = Map(Currency.getInstance("USD") -> 32),
        metadata = event1.metadata.copy(updatedAt = Some(now))
      )),
      None
    )
  }
