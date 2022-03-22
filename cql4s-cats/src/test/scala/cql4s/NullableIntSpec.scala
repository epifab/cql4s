package cql4s

import cats.effect.IO
import cql4s.dsl.*
import cql4s.test.schema.dummy
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class NullableIntSpec extends AnyFreeSpec with Matchers with CassandraAware:

  val select = Select
    .from(dummy)
    .take(_ => Option.empty[Int][nullable[int]])
    .where(_("id") === :?)
    .compile

  val insert = Insert.into(dummy).fields(_("id")).compile

  "Storing and retrieving a nullable integer" in {
    (for {
      given _ <- cassandra
      id <- IO(UUID.randomUUID())
      _ <- insert.execute(id)
      x <- select.one(id)
    } yield x shouldBe None).unsafeRunSync()
  }
