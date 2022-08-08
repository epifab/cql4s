package cql4s

import cql4s.dsl.*
import cql4s.test.schema.{dummy, plusone}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID


class UDFSpec extends AnyFreeSpec with Matchers with CassandraAware:
  "User defined functions can be called" in {
    (for {
      given _ <- cassandra

      _ <- Truncate(dummy).compile.execute(())
      _ <- DummyFixture.insert.execute(UUID.randomUUID())
      one <- Select.from(dummy).take(_ => plusone(1[int])).compile.one(())
    } yield one).unsafeRunSync() shouldBe 2
  }
