package cql4s

import cats.effect.IO
import cql4s.dsl.*
import cql4s.test.schema.all_types
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class NullableIntSpec extends AnyFreeSpec with Matchers with CassandraAware:

  val select = Select
    .from(all_types)
    .take(_("_nullableint"))
    .where(_("id") === :?)
    .compile

  val insert = Insert.into(all_types).fields { at => (
    at("id"),
    at("_nullableint")
  )}
  .compile

  "Storing and retrieving a nullable integer" in {
    (for {
      c <- cassandra
      id <- IO(UUID.randomUUID())
      _ <- c.execute(insert)((id, None))
      x <- c.one(select)(id)
    } yield x shouldBe None).unsafeRunSync()
  }
