package cql4s

import cats.effect.IO
import cql4s.dsl.*
import org.scalatest.Assertion
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Instant, LocalDate, LocalTime, ZoneOffset}
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

  val atSomePoint: Instant = Instant.parse("2021-03-14T15:09:00Z")
  val atSomePointStartOfTheDay: Instant = Instant.parse("2021-03-14T00:00:00Z")
  val atSomePointDate: LocalDate = LocalDate.of(2021, 3, 14)
  val atSomePointMinUuid: UUID = UUID.fromString("3495ce00-84d7-11eb-8080-808080808080")
  val atSomePointMaxUuid: UUID = UUID.fromString("3495f50f-84d7-11eb-7f7f-7f7f7f7f7f7f")

  "minTimeUuid()/maxTimeUuid()" in {
    result(select.take(_ => minTimeuuid(atSomePoint[timestamp])).compile) shouldBe atSomePointMinUuid
    result(select.take(_ => maxTimeuuid(atSomePoint[timestamp])).compile) shouldBe atSomePointMaxUuid
  }

  "toDate()" in {
    result(select.take(_ => toDate(atSomePoint[timestamp])).compile) shouldBe atSomePointDate
    result(select.take(_ => toDate(atSomePointMaxUuid[timeuuid])).compile) shouldBe atSomePointDate
  }

  "toTimestamp()" in {
    result(select.take(_ => toTimestamp(atSomePointDate[date])).compile) shouldBe atSomePointStartOfTheDay
    result(select.take(_ => toTimestamp(atSomePointMaxUuid[timeuuid])).compile) shouldBe atSomePoint
  }

  "toUnixTimestamp()" in {
    result(select.take(_ => toUnixTimestamp(atSomePointDate[date])).compile) shouldBe atSomePointStartOfTheDay.toEpochMilli
    result(select.take(_ => toUnixTimestamp(atSomePointMaxUuid[timeuuid])).compile) shouldBe atSomePoint.toEpochMilli
    result(select.take(_ => toUnixTimestamp(atSomePoint[timestamp])).compile) shouldBe atSomePoint.toEpochMilli
  }
