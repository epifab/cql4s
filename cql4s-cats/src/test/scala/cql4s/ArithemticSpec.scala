package cql4s

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cql4s.compiler.FieldFragment
import cql4s.dsl.*
import cql4s.test.CassandraTestConfig
import cql4s.test.schema.all_types
import org.scalatest.{Assertion, BeforeAndAfterAll}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class ArithemticSpec extends AnyFreeSpec with Matchers with CassandraAware:

  val select = Select.from(all_types).where(_("id") === :?)

  val insert = Insert.into(all_types)
    .fields { at => (
      at("id"),
      at("_tinyint"),
      at("_smallint"),
      at("_int"),
      at("_bigint"),
      at("_float"),
      at("_double"),
      at("_varint"),
      at("_decimal"),
    )}
    .compile
    .contramap((id: UUID) => (id, 42, 42, 42, 42, 42, 42, 42, 42))

  def testArithmetic[A](query: Query[UUID, A], result: A): Assertion =
    (for {
      c  <- cassandra
      id <- IO(UUID.randomUUID())
      _  <- c.execute(insert)(id)
      x  <- c.one(query)(id)
    } yield (x shouldBe result)).unsafeRunSync()

  "Add" - {
    "tinyint + " - {
      "tinyint"  in testArithmetic(select.take(x => x("_tinyint") + x("_tinyint")).compile, 84)
      "smallint" in testArithmetic(select.take(x => x("_tinyint") + x("_smallint")).compile, 84)
      "int"      in testArithmetic(select.take(x => x("_tinyint") + x("_int")).compile, 84)
      "bigint"   in testArithmetic(select.take(x => x("_tinyint") + x("_bigint")).compile, 84)
      "float"    in testArithmetic(select.take(x => x("_tinyint") + x("_float")).compile, 84)
      "double"   in testArithmetic(select.take(x => x("_tinyint") + x("_double")).compile, 84)
      "varint"   in testArithmetic(select.take(x => x("_tinyint") + x("_varint")).compile, 84)
      "decimal"  in testArithmetic(select.take(x => x("_tinyint") + x("_decimal")).compile, 84)
    }

    "smallint + " - {
      "tinyint"  in testArithmetic(select.take(x => x("_smallint") + x("_tinyint")).compile, 84)
      "smallint" in testArithmetic(select.take(x => x("_smallint") + x("_smallint")).compile, 84)
      "int"      in testArithmetic(select.take(x => x("_smallint") + x("_int")).compile, 84)
      "bigint"   in testArithmetic(select.take(x => x("_smallint") + x("_bigint")).compile, 84)
      "float"    in testArithmetic(select.take(x => x("_smallint") + x("_float")).compile, 84)
      "double"   in testArithmetic(select.take(x => x("_smallint") + x("_double")).compile, 84)
      "varint"   in testArithmetic(select.take(x => x("_smallint") + x("_varint")).compile, 84)
      "decimal"  in testArithmetic(select.take(x => x("_smallint") + x("_decimal")).compile, 84)
    }

    "int + " - {
      "tinyint"  in testArithmetic(select.take(x => x("_int") + x("_tinyint")).compile, 84)
      "smallint" in testArithmetic(select.take(x => x("_int") + x("_smallint")).compile, 84)
      "int"      in testArithmetic(select.take(x => x("_int") + x("_int")).compile, 84)
      "bigint"   in testArithmetic(select.take(x => x("_int") + x("_bigint")).compile, 84)
      "float"    in testArithmetic(select.take(x => x("_int") + x("_float")).compile, 84)
      "double"   in testArithmetic(select.take(x => x("_int") + x("_double")).compile, 84)
      "varint"   in testArithmetic(select.take(x => x("_int") + x("_varint")).compile, 84)
      "decimal"  in testArithmetic(select.take(x => x("_int") + x("_decimal")).compile, 84)
    }

    "bigint + " - {
      "tinyint"  in testArithmetic(select.take(x => x("_bigint") + x("_tinyint")).compile, 84)
      "smallint" in testArithmetic(select.take(x => x("_bigint") + x("_smallint")).compile, 84)
      "int"      in testArithmetic(select.take(x => x("_bigint") + x("_int")).compile, 84)
      "bigint"   in testArithmetic(select.take(x => x("_bigint") + x("_bigint")).compile, 84)
      "float"    in testArithmetic(select.take(x => x("_bigint") + x("_float")).compile, 84)
      "double"   in testArithmetic(select.take(x => x("_bigint") + x("_double")).compile, 84)
      "varint"   in testArithmetic(select.take(x => x("_bigint") + x("_varint")).compile, 84)
      "decimal"  in testArithmetic(select.take(x => x("_bigint") + x("_decimal")).compile, 84)
    }

    "float + " - {
      "tinyint"  in testArithmetic(select.take(x => x("_float") + x("_tinyint")).compile, 84)
      "smallint" in testArithmetic(select.take(x => x("_float") + x("_smallint")).compile, 84)
      "int"      in testArithmetic(select.take(x => x("_float") + x("_int")).compile, 84)
      "bigint"   in testArithmetic(select.take(x => x("_float") + x("_bigint")).compile, 84)
      "float"    in testArithmetic(select.take(x => x("_float") + x("_float")).compile, 84)
      "double"   in testArithmetic(select.take(x => x("_float") + x("_double")).compile, 84)
      "varint"   in testArithmetic(select.take(x => x("_float") + x("_varint")).compile, 84)
      "decimal"  in testArithmetic(select.take(x => x("_float") + x("_decimal")).compile, 84)
    }

    "double + " - {
      "tinyint"  in testArithmetic(select.take(x => x("_double") + x("_tinyint")).compile, 84)
      "smallint" in testArithmetic(select.take(x => x("_double") + x("_smallint")).compile, 84)
      "int"      in testArithmetic(select.take(x => x("_double") + x("_int")).compile, 84)
      "bigint"   in testArithmetic(select.take(x => x("_double") + x("_bigint")).compile, 84)
      "float"    in testArithmetic(select.take(x => x("_double") + x("_float")).compile, 84)
      "double"   in testArithmetic(select.take(x => x("_double") + x("_double")).compile, 84)
      "varint"   in testArithmetic(select.take(x => x("_double") + x("_varint")).compile, 84)
      "decimal"  in testArithmetic(select.take(x => x("_double") + x("_decimal")).compile, 84)
    }

    "varint + " - {
      "tinyint"  in testArithmetic(select.take(x => x("_varint") + x("_tinyint")).compile, 84)
      "smallint" in testArithmetic(select.take(x => x("_varint") + x("_smallint")).compile, 84)
      "int"      in testArithmetic(select.take(x => x("_varint") + x("_int")).compile, 84)
      "bigint"   in testArithmetic(select.take(x => x("_varint") + x("_bigint")).compile, 84)
      "float"    in testArithmetic(select.take(x => x("_varint") + x("_float")).compile, 84)
      "double"   in testArithmetic(select.take(x => x("_varint") + x("_double")).compile, 84)
      "varint"   in testArithmetic(select.take(x => x("_varint") + x("_varint")).compile, 84)
      "decimal"  in testArithmetic(select.take(x => x("_varint") + x("_decimal")).compile, 84)
    }

    "decimal + " - {
      "tinyint"  in testArithmetic(select.take(x => x("_decimal") + x("_tinyint")).compile, 84)
      "smallint" in testArithmetic(select.take(x => x("_decimal") + x("_smallint")).compile, 84)
      "int"      in testArithmetic(select.take(x => x("_decimal") + x("_int")).compile, 84)
      "bigint"   in testArithmetic(select.take(x => x("_decimal") + x("_bigint")).compile, 84)
      "float"    in testArithmetic(select.take(x => x("_decimal") + x("_float")).compile, 84)
      "double"   in testArithmetic(select.take(x => x("_decimal") + x("_double")).compile, 84)
      "varint"   in testArithmetic(select.take(x => x("_decimal") + x("_varint")).compile, 84)
      "decimal"  in testArithmetic(select.take(x => x("_decimal") + x("_decimal")).compile, 84)
    }
  }

  "Sub" - {
    "tinyint - " - {
      "tinyint"  in testArithmetic(select.take(x => x("_tinyint") - x("_tinyint")).compile, 0)
      "smallint" in testArithmetic(select.take(x => x("_tinyint") - x("_smallint")).compile, 0)
      "int"      in testArithmetic(select.take(x => x("_tinyint") - x("_int")).compile, 0)
      "bigint"   in testArithmetic(select.take(x => x("_tinyint") - x("_bigint")).compile, 0)
      "float"    in testArithmetic(select.take(x => x("_tinyint") - x("_float")).compile, 0)
      "double"   in testArithmetic(select.take(x => x("_tinyint") - x("_double")).compile, 0)
      "varint"   in testArithmetic(select.take(x => x("_tinyint") - x("_varint")).compile, 0)
      "decimal"  in testArithmetic(select.take(x => x("_tinyint") - x("_decimal")).compile, 0)
    }

    "smallint - " - {
      "tinyint"  in testArithmetic(select.take(x => x("_smallint") - x("_tinyint")).compile, 0)
      "smallint" in testArithmetic(select.take(x => x("_smallint") - x("_smallint")).compile, 0)
      "int"      in testArithmetic(select.take(x => x("_smallint") - x("_int")).compile, 0)
      "bigint"   in testArithmetic(select.take(x => x("_smallint") - x("_bigint")).compile, 0)
      "float"    in testArithmetic(select.take(x => x("_smallint") - x("_float")).compile, 0)
      "double"   in testArithmetic(select.take(x => x("_smallint") - x("_double")).compile, 0)
      "varint"   in testArithmetic(select.take(x => x("_smallint") - x("_varint")).compile, 0)
      "decimal"  in testArithmetic(select.take(x => x("_smallint") - x("_decimal")).compile, 0)
    }

    "int - " - {
      "tinyint"  in testArithmetic(select.take(x => x("_int") - x("_tinyint")).compile, 0)
      "smallint" in testArithmetic(select.take(x => x("_int") - x("_smallint")).compile, 0)
      "int"      in testArithmetic(select.take(x => x("_int") - x("_int")).compile, 0)
      "bigint"   in testArithmetic(select.take(x => x("_int") - x("_bigint")).compile, 0)
      "float"    in testArithmetic(select.take(x => x("_int") - x("_float")).compile, 0)
      "double"   in testArithmetic(select.take(x => x("_int") - x("_double")).compile, 0)
      "varint"   in testArithmetic(select.take(x => x("_int") - x("_varint")).compile, 0)
      "decimal"  in testArithmetic(select.take(x => x("_int") - x("_decimal")).compile, 0)
    }

    "bigint - " - {
      "tinyint"  in testArithmetic(select.take(x => x("_bigint") - x("_tinyint")).compile, 0)
      "smallint" in testArithmetic(select.take(x => x("_bigint") - x("_smallint")).compile, 0)
      "int"      in testArithmetic(select.take(x => x("_bigint") - x("_int")).compile, 0)
      "bigint"   in testArithmetic(select.take(x => x("_bigint") - x("_bigint")).compile, 0)
      "float"    in testArithmetic(select.take(x => x("_bigint") - x("_float")).compile, 0)
      "double"   in testArithmetic(select.take(x => x("_bigint") - x("_double")).compile, 0)
      "varint"   in testArithmetic(select.take(x => x("_bigint") - x("_varint")).compile, 0)
      "decimal"  in testArithmetic(select.take(x => x("_bigint") - x("_decimal")).compile, 0)
    }

    "float - " - {
      "tinyint"  in testArithmetic(select.take(x => x("_float") - x("_tinyint")).compile, 0)
      "smallint" in testArithmetic(select.take(x => x("_float") - x("_smallint")).compile, 0)
      "int"      in testArithmetic(select.take(x => x("_float") - x("_int")).compile, 0)
      "bigint"   in testArithmetic(select.take(x => x("_float") - x("_bigint")).compile, 0)
      "float"    in testArithmetic(select.take(x => x("_float") - x("_float")).compile, 0)
      "double"   in testArithmetic(select.take(x => x("_float") - x("_double")).compile, 0)
      "varint"   in testArithmetic(select.take(x => x("_float") - x("_varint")).compile, 0)
      "decimal"  in testArithmetic(select.take(x => x("_float") - x("_decimal")).compile, 0)
    }

    "double - " - {
      "tinyint"  in testArithmetic(select.take(x => x("_double") - x("_tinyint")).compile, 0)
      "smallint" in testArithmetic(select.take(x => x("_double") - x("_smallint")).compile, 0)
      "int"      in testArithmetic(select.take(x => x("_double") - x("_int")).compile, 0)
      "bigint"   in testArithmetic(select.take(x => x("_double") - x("_bigint")).compile, 0)
      "float"    in testArithmetic(select.take(x => x("_double") - x("_float")).compile, 0)
      "double"   in testArithmetic(select.take(x => x("_double") - x("_double")).compile, 0)
      "varint"   in testArithmetic(select.take(x => x("_double") - x("_varint")).compile, 0)
      "decimal"  in testArithmetic(select.take(x => x("_double") - x("_decimal")).compile, 0)
    }

    "varint - " - {
      "tinyint"  in testArithmetic(select.take(x => x("_varint") - x("_tinyint")).compile, 0)
      "smallint" in testArithmetic(select.take(x => x("_varint") - x("_smallint")).compile, 0)
      "int"      in testArithmetic(select.take(x => x("_varint") - x("_int")).compile, 0)
      "bigint"   in testArithmetic(select.take(x => x("_varint") - x("_bigint")).compile, 0)
      "float"    in testArithmetic(select.take(x => x("_varint") - x("_float")).compile, 0)
      "double"   in testArithmetic(select.take(x => x("_varint") - x("_double")).compile, 0)
      "varint"   in testArithmetic(select.take(x => x("_varint") - x("_varint")).compile, 0)
      "decimal"  in testArithmetic(select.take(x => x("_varint") - x("_decimal")).compile, 0)
    }

    "decimal - " - {
      "tinyint"  in testArithmetic(select.take(x => x("_decimal") - x("_tinyint")).compile, 0)
      "smallint" in testArithmetic(select.take(x => x("_decimal") - x("_smallint")).compile, 0)
      "int"      in testArithmetic(select.take(x => x("_decimal") - x("_int")).compile, 0)
      "bigint"   in testArithmetic(select.take(x => x("_decimal") - x("_bigint")).compile, 0)
      "float"    in testArithmetic(select.take(x => x("_decimal") - x("_float")).compile, 0)
      "double"   in testArithmetic(select.take(x => x("_decimal") - x("_double")).compile, 0)
      "varint"   in testArithmetic(select.take(x => x("_decimal") - x("_varint")).compile, 0)
      "decimal"  in testArithmetic(select.take(x => x("_decimal") - x("_decimal")).compile, 0)
    }
  }

  "Mul" - {
    "tinyint * " - {
      "tinyint"  in testArithmetic(select.take(x => x("_tinyint") * x("_tinyint")).compile, 1764.toByte)  // will overflow
      "smallint" in testArithmetic(select.take(x => x("_tinyint") * x("_smallint")).compile, 1764)
      "int"      in testArithmetic(select.take(x => x("_tinyint") * x("_int")).compile, 1764)
      "bigint"   in testArithmetic(select.take(x => x("_tinyint") * x("_bigint")).compile, 1764)
      "float"    in testArithmetic(select.take(x => x("_tinyint") * x("_float")).compile, 1764)
      "double"   in testArithmetic(select.take(x => x("_tinyint") * x("_double")).compile, 1764)
      "varint"   in testArithmetic(select.take(x => x("_tinyint") * x("_varint")).compile, 1764)
      "decimal"  in testArithmetic(select.take(x => x("_tinyint") * x("_decimal")).compile, 1764)
    }

    "smallint * " - {
      "tinyint"  in testArithmetic(select.take(x => x("_smallint") * x("_tinyint")).compile, 1764)
      "smallint" in testArithmetic(select.take(x => x("_smallint") * x("_smallint")).compile, 1764)
      "int"      in testArithmetic(select.take(x => x("_smallint") * x("_int")).compile, 1764)
      "bigint"   in testArithmetic(select.take(x => x("_smallint") * x("_bigint")).compile, 1764)
      "float"    in testArithmetic(select.take(x => x("_smallint") * x("_float")).compile, 1764)
      "double"   in testArithmetic(select.take(x => x("_smallint") * x("_double")).compile, 1764)
      "varint"   in testArithmetic(select.take(x => x("_smallint") * x("_varint")).compile, 1764)
      "decimal"  in testArithmetic(select.take(x => x("_smallint") * x("_decimal")).compile, 1764)
    }

    "int * " - {
      "tinyint"  in testArithmetic(select.take(x => x("_int") * x("_tinyint")).compile, 1764)
      "smallint" in testArithmetic(select.take(x => x("_int") * x("_smallint")).compile, 1764)
      "int"      in testArithmetic(select.take(x => x("_int") * x("_int")).compile, 1764)
      "bigint"   in testArithmetic(select.take(x => x("_int") * x("_bigint")).compile, 1764)
      "float"    in testArithmetic(select.take(x => x("_int") * x("_float")).compile, 1764)
      "double"   in testArithmetic(select.take(x => x("_int") * x("_double")).compile, 1764)
      "varint"   in testArithmetic(select.take(x => x("_int") * x("_varint")).compile, 1764)
      "decimal"  in testArithmetic(select.take(x => x("_int") * x("_decimal")).compile, 1764)
    }

    "bigint * " - {
      "tinyint"  in testArithmetic(select.take(x => x("_bigint") * x("_tinyint")).compile, 1764)
      "smallint" in testArithmetic(select.take(x => x("_bigint") * x("_smallint")).compile, 1764)
      "int"      in testArithmetic(select.take(x => x("_bigint") * x("_int")).compile, 1764)
      "bigint"   in testArithmetic(select.take(x => x("_bigint") * x("_bigint")).compile, 1764)
      "float"    in testArithmetic(select.take(x => x("_bigint") * x("_float")).compile, 1764)
      "double"   in testArithmetic(select.take(x => x("_bigint") * x("_double")).compile, 1764)
      "varint"   in testArithmetic(select.take(x => x("_bigint") * x("_varint")).compile, 1764)
      "decimal"  in testArithmetic(select.take(x => x("_bigint") * x("_decimal")).compile, 1764)
    }

    "float * " - {
      "tinyint"  in testArithmetic(select.take(x => x("_float") * x("_tinyint")).compile, 1764)
      "smallint" in testArithmetic(select.take(x => x("_float") * x("_smallint")).compile, 1764)
      "int"      in testArithmetic(select.take(x => x("_float") * x("_int")).compile, 1764)
      "bigint"   in testArithmetic(select.take(x => x("_float") * x("_bigint")).compile, 1764)
      "float"    in testArithmetic(select.take(x => x("_float") * x("_float")).compile, 1764)
      "double"   in testArithmetic(select.take(x => x("_float") * x("_double")).compile, 1764)
      "varint"   in testArithmetic(select.take(x => x("_float") * x("_varint")).compile, 1764)
      "decimal"  in testArithmetic(select.take(x => x("_float") * x("_decimal")).compile, 1764)
    }

    "double * " - {
      "tinyint"  in testArithmetic(select.take(x => x("_double") * x("_tinyint")).compile, 1764)
      "smallint" in testArithmetic(select.take(x => x("_double") * x("_smallint")).compile, 1764)
      "int"      in testArithmetic(select.take(x => x("_double") * x("_int")).compile, 1764)
      "bigint"   in testArithmetic(select.take(x => x("_double") * x("_bigint")).compile, 1764)
      "float"    in testArithmetic(select.take(x => x("_double") * x("_float")).compile, 1764)
      "double"   in testArithmetic(select.take(x => x("_double") * x("_double")).compile, 1764)
      "varint"   in testArithmetic(select.take(x => x("_double") * x("_varint")).compile, 1764)
      "decimal"  in testArithmetic(select.take(x => x("_double") * x("_decimal")).compile, 1764)
    }

    "varint * " - {
      "tinyint"  in testArithmetic(select.take(x => x("_varint") * x("_tinyint")).compile, 1764)
      "smallint" in testArithmetic(select.take(x => x("_varint") * x("_smallint")).compile, 1764)
      "int"      in testArithmetic(select.take(x => x("_varint") * x("_int")).compile, 1764)
      "bigint"   in testArithmetic(select.take(x => x("_varint") * x("_bigint")).compile, 1764)
      "float"    in testArithmetic(select.take(x => x("_varint") * x("_float")).compile, 1764)
      "double"   in testArithmetic(select.take(x => x("_varint") * x("_double")).compile, 1764)
      "varint"   in testArithmetic(select.take(x => x("_varint") * x("_varint")).compile, 1764)
      "decimal"  in testArithmetic(select.take(x => x("_varint") * x("_decimal")).compile, 1764)
    }

    "decimal * " - {
      "tinyint"  in testArithmetic(select.take(x => x("_decimal") * x("_tinyint")).compile, 1764)
      "smallint" in testArithmetic(select.take(x => x("_decimal") * x("_smallint")).compile, 1764)
      "int"      in testArithmetic(select.take(x => x("_decimal") * x("_int")).compile, 1764)
      "bigint"   in testArithmetic(select.take(x => x("_decimal") * x("_bigint")).compile, 1764)
      "float"    in testArithmetic(select.take(x => x("_decimal") * x("_float")).compile, 1764)
      "double"   in testArithmetic(select.take(x => x("_decimal") * x("_double")).compile, 1764)
      "varint"   in testArithmetic(select.take(x => x("_decimal") * x("_varint")).compile, 1764)
      "decimal"  in testArithmetic(select.take(x => x("_decimal") * x("_decimal")).compile, 1764)
    }
  }

  "Div" - {
    "tinyint / " - {
      "tinyint"  in testArithmetic(select.take(x => x("_tinyint") / x("_tinyint")).compile, 1)
      "smallint" in testArithmetic(select.take(x => x("_tinyint") / x("_smallint")).compile, 1)
      "int"      in testArithmetic(select.take(x => x("_tinyint") / x("_int")).compile, 1)
      "bigint"   in testArithmetic(select.take(x => x("_tinyint") / x("_bigint")).compile, 1)
      "float"    in testArithmetic(select.take(x => x("_tinyint") / x("_float")).compile, 1)
      "double"   in testArithmetic(select.take(x => x("_tinyint") / x("_double")).compile, 1)
      "varint"   in testArithmetic(select.take(x => x("_tinyint") / x("_varint")).compile, 1)
      "decimal"  in testArithmetic(select.take(x => x("_tinyint") / x("_decimal")).compile, 1)
    }

    "smallint / " - {
      "tinyint"  in testArithmetic(select.take(x => x("_smallint") / x("_tinyint")).compile, 1)
      "smallint" in testArithmetic(select.take(x => x("_smallint") / x("_smallint")).compile, 1)
      "int"      in testArithmetic(select.take(x => x("_smallint") / x("_int")).compile, 1)
      "bigint"   in testArithmetic(select.take(x => x("_smallint") / x("_bigint")).compile, 1)
      "float"    in testArithmetic(select.take(x => x("_smallint") / x("_float")).compile, 1)
      "double"   in testArithmetic(select.take(x => x("_smallint") / x("_double")).compile, 1)
      "varint"   in testArithmetic(select.take(x => x("_smallint") / x("_varint")).compile, 1)
      "decimal"  in testArithmetic(select.take(x => x("_smallint") / x("_decimal")).compile, 1)
    }

    "int / " - {
      "tinyint"  in testArithmetic(select.take(x => x("_int") / x("_tinyint")).compile, 1)
      "smallint" in testArithmetic(select.take(x => x("_int") / x("_smallint")).compile, 1)
      "int"      in testArithmetic(select.take(x => x("_int") / x("_int")).compile, 1)
      "bigint"   in testArithmetic(select.take(x => x("_int") / x("_bigint")).compile, 1)
      "float"    in testArithmetic(select.take(x => x("_int") / x("_float")).compile, 1)
      "double"   in testArithmetic(select.take(x => x("_int") / x("_double")).compile, 1)
      "varint"   in testArithmetic(select.take(x => x("_int") / x("_varint")).compile, 1)
      "decimal"  in testArithmetic(select.take(x => x("_int") / x("_decimal")).compile, 1)
    }

    "bigint / " - {
      "tinyint"  in testArithmetic(select.take(x => x("_bigint") / x("_tinyint")).compile, 1)
      "smallint" in testArithmetic(select.take(x => x("_bigint") / x("_smallint")).compile, 1)
      "int"      in testArithmetic(select.take(x => x("_bigint") / x("_int")).compile, 1)
      "bigint"   in testArithmetic(select.take(x => x("_bigint") / x("_bigint")).compile, 1)
      "float"    in testArithmetic(select.take(x => x("_bigint") / x("_float")).compile, 1)
      "double"   in testArithmetic(select.take(x => x("_bigint") / x("_double")).compile, 1)
      "varint"   in testArithmetic(select.take(x => x("_bigint") / x("_varint")).compile, 1)
      "decimal"  in testArithmetic(select.take(x => x("_bigint") / x("_decimal")).compile, 1)
    }

    "float / " - {
      "tinyint"  in testArithmetic(select.take(x => x("_float") / x("_tinyint")).compile, 1)
      "smallint" in testArithmetic(select.take(x => x("_float") / x("_smallint")).compile, 1)
      "int"      in testArithmetic(select.take(x => x("_float") / x("_int")).compile, 1)
      "bigint"   in testArithmetic(select.take(x => x("_float") / x("_bigint")).compile, 1)
      "float"    in testArithmetic(select.take(x => x("_float") / x("_float")).compile, 1)
      "double"   in testArithmetic(select.take(x => x("_float") / x("_double")).compile, 1)
      "varint"   in testArithmetic(select.take(x => x("_float") / x("_varint")).compile, 1)
      "decimal"  in testArithmetic(select.take(x => x("_float") / x("_decimal")).compile, 1)
    }

    "double / " - {
      "tinyint"  in testArithmetic(select.take(x => x("_double") / x("_tinyint")).compile, 1)
      "smallint" in testArithmetic(select.take(x => x("_double") / x("_smallint")).compile, 1)
      "int"      in testArithmetic(select.take(x => x("_double") / x("_int")).compile, 1)
      "bigint"   in testArithmetic(select.take(x => x("_double") / x("_bigint")).compile, 1)
      "float"    in testArithmetic(select.take(x => x("_double") / x("_float")).compile, 1)
      "double"   in testArithmetic(select.take(x => x("_double") / x("_double")).compile, 1)
      "varint"   in testArithmetic(select.take(x => x("_double") / x("_varint")).compile, 1)
      "decimal"  in testArithmetic(select.take(x => x("_double") / x("_decimal")).compile, 1)
    }

    "varint / " - {
      "tinyint"  in testArithmetic(select.take(x => x("_varint") / x("_tinyint")).compile, 1)
      "smallint" in testArithmetic(select.take(x => x("_varint") / x("_smallint")).compile, 1)
      "int"      in testArithmetic(select.take(x => x("_varint") / x("_int")).compile, 1)
      "bigint"   in testArithmetic(select.take(x => x("_varint") / x("_bigint")).compile, 1)
      "float"    in testArithmetic(select.take(x => x("_varint") / x("_float")).compile, 1)
      "double"   in testArithmetic(select.take(x => x("_varint") / x("_double")).compile, 1)
      "varint"   in testArithmetic(select.take(x => x("_varint") / x("_varint")).compile, 1)
      "decimal"  in testArithmetic(select.take(x => x("_varint") / x("_decimal")).compile, 1)
    }

    "decimal / " - {
      "tinyint"  in testArithmetic(select.take(x => x("_decimal") / x("_tinyint")).compile, 1)
      "smallint" in testArithmetic(select.take(x => x("_decimal") / x("_smallint")).compile, 1)
      "int"      in testArithmetic(select.take(x => x("_decimal") / x("_int")).compile, 1)
      "bigint"   in testArithmetic(select.take(x => x("_decimal") / x("_bigint")).compile, 1)
      "float"    in testArithmetic(select.take(x => x("_decimal") / x("_float")).compile, 1)
      "double"   in testArithmetic(select.take(x => x("_decimal") / x("_double")).compile, 1)
      "varint"   in testArithmetic(select.take(x => x("_decimal") / x("_varint")).compile, 1)
      "decimal"  in testArithmetic(select.take(x => x("_decimal") / x("_decimal")).compile, 1)
    }
  }
