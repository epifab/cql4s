package cql4s

import cats.effect.IO
import cql4s.dsl.*
import cql4s.dsl.functions.*
import org.scalatest.Assertion
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.{LocalDate, LocalTime, Instant}
import java.util.UUID

class FunctionsIntegrationSpec extends AnyFreeSpec with Matchers with CassandraAware:
  import DummyFixture.*

  def result[T, A](query: Query[UUID, A]): A =
    (for {
      given _ <- cassandra
      id <- IO(UUID.randomUUID())
      _  <- insert.execute(id)
      x  <- query.one(id)
    } yield x).unsafeRunSync()

  "now()" in {
    result(select.take(_ => now()).compile) shouldBe a[UUID]
  }

  "currentTimeUUID()" in {
    result(select.take(_ => currentTimeUUID()).compile) shouldBe a[UUID]
  }

  "currentTimestamp()" in {
    result(select.take(_ => currentTimestamp()).compile) shouldBe a[Instant]
  }

  "currentDate()" in {
    result(select.take(_ => currentDate()).compile) shouldBe a[LocalDate]
  }

  "currentTime()" in {
    result(select.take(_ => currentTime()).compile) shouldBe a[LocalTime]
  }

  "minTimeUuid/maxTimeUuid" in {
    result(select.take(_ => minTimeuuid(Instant.parse("2021-03-14T15:09:00Z")[timestamp])).compile) shouldBe UUID.fromString("3495ce00-84d7-11eb-8080-808080808080")
    result(select.take(_ => maxTimeuuid(Instant.parse("2021-03-14T15:09:00Z")[timestamp])).compile) shouldBe UUID.fromString("3495f50f-84d7-11eb-7f7f-7f7f7f7f7f7f")
  }
