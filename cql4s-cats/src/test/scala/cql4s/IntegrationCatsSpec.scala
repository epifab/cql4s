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

class IntegrationCatsSpec extends AnyFreeSpec with Matchers:

  val cassandraRuntime: Resource[IO, CassandraCatsRuntime[IO]] = CassandraCatsRuntime[IO](CassandraTestConfig)
  implicit val ioRuntime: IORuntime = IORuntime.global

  "Can insert / retrieve data" in {
    val result: IO[(Option[Event], Option[Event])] = cassandraRuntime.use(cassandra =>
      for {
        _ <- cassandra.execute(truncateEvents)(())
        _ <- cassandra.executeBatch(insertEvent, BatchType.LOGGED)(List(event1, event2))
        _ <- cassandra.execute(findEventById)(event1.id).evalTap(e => cassandra.execute(updateEventTickets)((
          Map(Currency.getInstance("USD") -> 32),
          e.metadata.copy(updatedAt = Some(now)),
          e.id
        ))).compile.drain
        updatedEvent <- cassandra.execute(findEventById)(event1.id).compile.last
        _ <- cassandra.execute(deleteEvent)(event1.id)
        deletedEvent <- cassandra.execute(findEventById)(event1.id).compile.last
      } yield (updatedEvent, deletedEvent)
    )

    result.unsafeRunSync() shouldBe (
      Some(event1.copy(
        tickets = Map(Currency.getInstance("USD") -> 32),
        metadata = event1.metadata.copy(updatedAt = Some(now))
      )),
      None
    )
  }
