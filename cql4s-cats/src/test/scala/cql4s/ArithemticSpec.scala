package cql4s

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cql4s.dsl.*
import cql4s.test.CassandraTestConfig
import cql4s.test.schema.dummy
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.Assertion

import java.util.UUID

class ArithemticSpec extends AnyFreeSpec with Matchers with CassandraAware:

  import DummyFixture.*

  private val tinyint42 = 42.toByte[tinyint]
  private val smallint42 = 42.toShort[smallint]
  private val int42 = 42[int]
  private val bigint42 = 42L[bigint]
  private val float42 = 42.toFloat[float]
  private val double42 = 42.toDouble[double]
  private val varint42 = BigInt(42)[varint]
  private val decimal42 = BigDecimal(42)[decimal]

  def testArithmetic[A](query: Query[UUID, A], result: A): Assertion =
    (for {
      given _ <- cassandra
      id <- IO(UUID.randomUUID())
      _  <- insert.execute(id)
      x  <- query.one(id)
    } yield (x shouldBe result)).unsafeRunSync()

  "Add" - {
    "tinyint + " - {
      "tinyint"  in testArithmetic(select.take(_ => int42 + tinyint42).compile, 84)
      "smallint" in testArithmetic(select.take(_ => int42 + smallint42).compile, 84)
      "int"      in testArithmetic(select.take(_ => int42 + int42).compile, 84)
      "bigint"   in testArithmetic(select.take(_ => int42 + bigint42).compile, 84)
      "float"    in testArithmetic(select.take(_ => int42 + float42).compile, 84)
      "double"   in testArithmetic(select.take(_ => int42 + double42).compile, 84)
      "varint"   in testArithmetic(select.take(_ => int42 + varint42).compile, 84)
      "decimal"  in testArithmetic(select.take(_ => int42 + decimal42).compile, 84)
    }

    "smallint + " - {
      "tinyint"  in testArithmetic(select.take(_ => smallint42 + tinyint42).compile, 84)
      "smallint" in testArithmetic(select.take(_ => smallint42 + smallint42).compile, 84)
      "int"      in testArithmetic(select.take(_ => smallint42 + int42).compile, 84)
      "bigint"   in testArithmetic(select.take(_ => smallint42 + bigint42).compile, 84)
      "float"    in testArithmetic(select.take(_ => smallint42 + float42).compile, 84)
      "double"   in testArithmetic(select.take(_ => smallint42 + double42).compile, 84)
      "varint"   in testArithmetic(select.take(_ => smallint42 + varint42).compile, 84)
      "decimal"  in testArithmetic(select.take(_ => smallint42 + decimal42).compile, 84)
    }

    "int + " - {
      "tinyint"  in testArithmetic(select.take(_ => int42 + tinyint42).compile, 84)
      "smallint" in testArithmetic(select.take(_ => int42 + smallint42).compile, 84)
      "int"      in testArithmetic(select.take(_ => int42 + int42).compile, 84)
      "bigint"   in testArithmetic(select.take(_ => int42 + bigint42).compile, 84)
      "float"    in testArithmetic(select.take(_ => int42 + float42).compile, 84)
      "double"   in testArithmetic(select.take(_ => int42 + double42).compile, 84)
      "varint"   in testArithmetic(select.take(_ => int42 + varint42).compile, 84)
      "decimal"  in testArithmetic(select.take(_ => int42 + decimal42).compile, 84)
    }

    "bigint + " - {
      "tinyint"  in testArithmetic(select.take(_ => bigint42 + tinyint42).compile, 84)
      "smallint" in testArithmetic(select.take(_ => bigint42 + smallint42).compile, 84)
      "int"      in testArithmetic(select.take(_ => bigint42 + int42).compile, 84)
      "bigint"   in testArithmetic(select.take(_ => bigint42 + bigint42).compile, 84)
      "float"    in testArithmetic(select.take(_ => bigint42 + float42).compile, 84)
      "double"   in testArithmetic(select.take(_ => bigint42 + double42).compile, 84)
      "varint"   in testArithmetic(select.take(_ => bigint42 + varint42).compile, 84)
      "decimal"  in testArithmetic(select.take(_ => bigint42 + decimal42).compile, 84)
    }

    "float + " - {
      "tinyint"  in testArithmetic(select.take(_ => float42 + tinyint42).compile, 84)
      "smallint" in testArithmetic(select.take(_ => float42 + smallint42).compile, 84)
      "int"      in testArithmetic(select.take(_ => float42 + int42).compile, 84)
      "bigint"   in testArithmetic(select.take(_ => float42 + bigint42).compile, 84)
      "float"    in testArithmetic(select.take(_ => float42 + float42).compile, 84)
      "double"   in testArithmetic(select.take(_ => float42 + double42).compile, 84)
      "varint"   in testArithmetic(select.take(_ => float42 + varint42).compile, 84)
      "decimal"  in testArithmetic(select.take(_ => float42 + decimal42).compile, 84)
    }

    "double + " - {
      "tinyint"  in testArithmetic(select.take(_ => double42 + tinyint42).compile, 84)
      "smallint" in testArithmetic(select.take(_ => double42 + smallint42).compile, 84)
      "int"      in testArithmetic(select.take(_ => double42 + int42).compile, 84)
      "bigint"   in testArithmetic(select.take(_ => double42 + bigint42).compile, 84)
      "float"    in testArithmetic(select.take(_ => double42 + float42).compile, 84)
      "double"   in testArithmetic(select.take(_ => double42 + double42).compile, 84)
      "varint"   in testArithmetic(select.take(_ => double42 + varint42).compile, 84)
      "decimal"  in testArithmetic(select.take(_ => double42 + decimal42).compile, 84)
    }

    "varint + " - {
      "tinyint"  in testArithmetic(select.take(_ => varint42 + tinyint42).compile, 84)
      "smallint" in testArithmetic(select.take(_ => varint42 + smallint42).compile, 84)
      "int"      in testArithmetic(select.take(_ => varint42 + int42).compile, 84)
      "bigint"   in testArithmetic(select.take(_ => varint42 + bigint42).compile, 84)
      "float"    in testArithmetic(select.take(_ => varint42 + float42).compile, 84)
      "double"   in testArithmetic(select.take(_ => varint42 + double42).compile, 84)
      "varint"   in testArithmetic(select.take(_ => varint42 + varint42).compile, 84)
      "decimal"  in testArithmetic(select.take(_ => varint42 + decimal42).compile, 84)
    }

    "decimal + " - {
      "tinyint"  in testArithmetic(select.take(_ => decimal42 + tinyint42).compile, 84)
      "smallint" in testArithmetic(select.take(_ => decimal42 + smallint42).compile, 84)
      "int"      in testArithmetic(select.take(_ => decimal42 + int42).compile, 84)
      "bigint"   in testArithmetic(select.take(_ => decimal42 + bigint42).compile, 84)
      "float"    in testArithmetic(select.take(_ => decimal42 + float42).compile, 84)
      "double"   in testArithmetic(select.take(_ => decimal42 + double42).compile, 84)
      "varint"   in testArithmetic(select.take(_ => decimal42 + varint42).compile, 84)
      "decimal"  in testArithmetic(select.take(_ => decimal42 + decimal42).compile, 84)
    }
  }

  "Sub" - {
    "tinyint - " - {
      "tinyint"  in testArithmetic(select.take(_ => tinyint42 - tinyint42).compile, 0)
      "smallint" in testArithmetic(select.take(_ => tinyint42 - smallint42).compile, 0)
      "int"      in testArithmetic(select.take(_ => tinyint42 - int42).compile, 0)
      "bigint"   in testArithmetic(select.take(_ => tinyint42 - bigint42).compile, 0)
      "float"    in testArithmetic(select.take(_ => tinyint42 - float42).compile, 0)
      "double"   in testArithmetic(select.take(_ => tinyint42 - double42).compile, 0)
      "varint"   in testArithmetic(select.take(_ => tinyint42 - varint42).compile, 0)
      "decimal"  in testArithmetic(select.take(_ => tinyint42 - decimal42).compile, 0)
    }

    "smallint - " - {
      "tinyint"  in testArithmetic(select.take(_ => smallint42 - tinyint42).compile, 0)
      "smallint" in testArithmetic(select.take(_ => smallint42 - smallint42).compile, 0)
      "int"      in testArithmetic(select.take(_ => smallint42 - int42).compile, 0)
      "bigint"   in testArithmetic(select.take(_ => smallint42 - bigint42).compile, 0)
      "float"    in testArithmetic(select.take(_ => smallint42 - float42).compile, 0)
      "double"   in testArithmetic(select.take(_ => smallint42 - double42).compile, 0)
      "varint"   in testArithmetic(select.take(_ => smallint42 - varint42).compile, 0)
      "decimal"  in testArithmetic(select.take(_ => smallint42 - decimal42).compile, 0)
    }

    "int - " - {
      "tinyint"  in testArithmetic(select.take(_ => int42 - tinyint42).compile, 0)
      "smallint" in testArithmetic(select.take(_ => int42 - smallint42).compile, 0)
      "int"      in testArithmetic(select.take(_ => int42 - int42).compile, 0)
      "bigint"   in testArithmetic(select.take(_ => int42 - bigint42).compile, 0)
      "float"    in testArithmetic(select.take(_ => int42 - float42).compile, 0)
      "double"   in testArithmetic(select.take(_ => int42 - double42).compile, 0)
      "varint"   in testArithmetic(select.take(_ => int42 - varint42).compile, 0)
      "decimal"  in testArithmetic(select.take(_ => int42 - decimal42).compile, 0)
    }

    "bigint - " - {
      "tinyint"  in testArithmetic(select.take(_ => bigint42 - tinyint42).compile, 0)
      "smallint" in testArithmetic(select.take(_ => bigint42 - smallint42).compile, 0)
      "int"      in testArithmetic(select.take(_ => bigint42 - int42).compile, 0)
      "bigint"   in testArithmetic(select.take(_ => bigint42 - bigint42).compile, 0)
      "float"    in testArithmetic(select.take(_ => bigint42 - float42).compile, 0)
      "double"   in testArithmetic(select.take(_ => bigint42 - double42).compile, 0)
      "varint"   in testArithmetic(select.take(_ => bigint42 - varint42).compile, 0)
      "decimal"  in testArithmetic(select.take(_ => bigint42 - decimal42).compile, 0)
    }

    "float - " - {
      "tinyint"  in testArithmetic(select.take(_ => float42 - tinyint42).compile, 0)
      "smallint" in testArithmetic(select.take(_ => float42 - smallint42).compile, 0)
      "int"      in testArithmetic(select.take(_ => float42 - int42).compile, 0)
      "bigint"   in testArithmetic(select.take(_ => float42 - bigint42).compile, 0)
      "float"    in testArithmetic(select.take(_ => float42 - float42).compile, 0)
      "double"   in testArithmetic(select.take(_ => float42 - double42).compile, 0)
      "varint"   in testArithmetic(select.take(_ => float42 - varint42).compile, 0)
      "decimal"  in testArithmetic(select.take(_ => float42 - decimal42).compile, 0)
    }

    "double - " - {
      "tinyint"  in testArithmetic(select.take(_ => double42 - tinyint42).compile, 0)
      "smallint" in testArithmetic(select.take(_ => double42 - smallint42).compile, 0)
      "int"      in testArithmetic(select.take(_ => double42 - int42).compile, 0)
      "bigint"   in testArithmetic(select.take(_ => double42 - bigint42).compile, 0)
      "float"    in testArithmetic(select.take(_ => double42 - float42).compile, 0)
      "double"   in testArithmetic(select.take(_ => double42 - double42).compile, 0)
      "varint"   in testArithmetic(select.take(_ => double42 - varint42).compile, 0)
      "decimal"  in testArithmetic(select.take(_ => double42 - decimal42).compile, 0)
    }

    "varint - " - {
      "tinyint"  in testArithmetic(select.take(_ => varint42 - tinyint42).compile, 0)
      "smallint" in testArithmetic(select.take(_ => varint42 - smallint42).compile, 0)
      "int"      in testArithmetic(select.take(_ => varint42 - int42).compile, 0)
      "bigint"   in testArithmetic(select.take(_ => varint42 - bigint42).compile, 0)
      "float"    in testArithmetic(select.take(_ => varint42 - float42).compile, 0)
      "double"   in testArithmetic(select.take(_ => varint42 - double42).compile, 0)
      "varint"   in testArithmetic(select.take(_ => varint42 - varint42).compile, 0)
      "decimal"  in testArithmetic(select.take(_ => varint42 - decimal42).compile, 0)
    }

    "decimal - " - {
      "tinyint"  in testArithmetic(select.take(_ => decimal42 - tinyint42).compile, 0)
      "smallint" in testArithmetic(select.take(_ => decimal42 - smallint42).compile, 0)
      "int"      in testArithmetic(select.take(_ => decimal42 - int42).compile, 0)
      "bigint"   in testArithmetic(select.take(_ => decimal42 - bigint42).compile, 0)
      "float"    in testArithmetic(select.take(_ => decimal42 - float42).compile, 0)
      "double"   in testArithmetic(select.take(_ => decimal42 - double42).compile, 0)
      "varint"   in testArithmetic(select.take(_ => decimal42 - varint42).compile, 0)
      "decimal"  in testArithmetic(select.take(_ => decimal42 - decimal42).compile, 0)
    }
  }

  "Mul" - {
    "tinyint * " - {
      "tinyint"  in testArithmetic(select.take(_ => tinyint42 * tinyint42).compile, 1764.toByte)  // will overflow
      "smallint" in testArithmetic(select.take(_ => tinyint42 * smallint42).compile, 1764)
      "int"      in testArithmetic(select.take(_ => tinyint42 * int42).compile, 1764)
      "bigint"   in testArithmetic(select.take(_ => tinyint42 * bigint42).compile, 1764)
      "float"    in testArithmetic(select.take(_ => tinyint42 * float42).compile, 1764)
      "double"   in testArithmetic(select.take(_ => tinyint42 * double42).compile, 1764)
      "varint"   in testArithmetic(select.take(_ => tinyint42 * varint42).compile, 1764)
      "decimal"  in testArithmetic(select.take(_ => tinyint42 * decimal42).compile, 1764)
    }

    "smallint * " - {
      "tinyint"  in testArithmetic(select.take(_ => smallint42 * tinyint42).compile, 1764)
      "smallint" in testArithmetic(select.take(_ => smallint42 * smallint42).compile, 1764)
      "int"      in testArithmetic(select.take(_ => smallint42 * int42).compile, 1764)
      "bigint"   in testArithmetic(select.take(_ => smallint42 * bigint42).compile, 1764)
      "float"    in testArithmetic(select.take(_ => smallint42 * float42).compile, 1764)
      "double"   in testArithmetic(select.take(_ => smallint42 * double42).compile, 1764)
      "varint"   in testArithmetic(select.take(_ => smallint42 * varint42).compile, 1764)
      "decimal"  in testArithmetic(select.take(_ => smallint42 * decimal42).compile, 1764)
    }

    "int * " - {
      "tinyint"  in testArithmetic(select.take(_ => int42 * tinyint42).compile, 1764)
      "smallint" in testArithmetic(select.take(_ => int42 * smallint42).compile, 1764)
      "int"      in testArithmetic(select.take(_ => int42 * int42).compile, 1764)
      "bigint"   in testArithmetic(select.take(_ => int42 * bigint42).compile, 1764)
      "float"    in testArithmetic(select.take(_ => int42 * float42).compile, 1764)
      "double"   in testArithmetic(select.take(_ => int42 * double42).compile, 1764)
      "varint"   in testArithmetic(select.take(_ => int42 * varint42).compile, 1764)
      "decimal"  in testArithmetic(select.take(_ => int42 * decimal42).compile, 1764)
    }

    "bigint * " - {
      "tinyint"  in testArithmetic(select.take(_ => bigint42 * tinyint42).compile, 1764)
      "smallint" in testArithmetic(select.take(_ => bigint42 * smallint42).compile, 1764)
      "int"      in testArithmetic(select.take(_ => bigint42 * int42).compile, 1764)
      "bigint"   in testArithmetic(select.take(_ => bigint42 * bigint42).compile, 1764)
      "float"    in testArithmetic(select.take(_ => bigint42 * float42).compile, 1764)
      "double"   in testArithmetic(select.take(_ => bigint42 * double42).compile, 1764)
      "varint"   in testArithmetic(select.take(_ => bigint42 * varint42).compile, 1764)
      "decimal"  in testArithmetic(select.take(_ => bigint42 * decimal42).compile, 1764)
    }

    "float * " - {
      "tinyint"  in testArithmetic(select.take(_ => float42 * tinyint42).compile, 1764)
      "smallint" in testArithmetic(select.take(_ => float42 * smallint42).compile, 1764)
      "int"      in testArithmetic(select.take(_ => float42 * int42).compile, 1764)
      "bigint"   in testArithmetic(select.take(_ => float42 * bigint42).compile, 1764)
      "float"    in testArithmetic(select.take(_ => float42 * float42).compile, 1764)
      "double"   in testArithmetic(select.take(_ => float42 * double42).compile, 1764)
      "varint"   in testArithmetic(select.take(_ => float42 * varint42).compile, 1764)
      "decimal"  in testArithmetic(select.take(_ => float42 * decimal42).compile, 1764)
    }

    "double * " - {
      "tinyint"  in testArithmetic(select.take(_ => double42 * tinyint42).compile, 1764)
      "smallint" in testArithmetic(select.take(_ => double42 * smallint42).compile, 1764)
      "int"      in testArithmetic(select.take(_ => double42 * int42).compile, 1764)
      "bigint"   in testArithmetic(select.take(_ => double42 * bigint42).compile, 1764)
      "float"    in testArithmetic(select.take(_ => double42 * float42).compile, 1764)
      "double"   in testArithmetic(select.take(_ => double42 * double42).compile, 1764)
      "varint"   in testArithmetic(select.take(_ => double42 * varint42).compile, 1764)
      "decimal"  in testArithmetic(select.take(_ => double42 * decimal42).compile, 1764)
    }

    "varint * " - {
      "tinyint"  in testArithmetic(select.take(_ => varint42 * tinyint42).compile, 1764)
      "smallint" in testArithmetic(select.take(_ => varint42 * smallint42).compile, 1764)
      "int"      in testArithmetic(select.take(_ => varint42 * int42).compile, 1764)
      "bigint"   in testArithmetic(select.take(_ => varint42 * bigint42).compile, 1764)
      "float"    in testArithmetic(select.take(_ => varint42 * float42).compile, 1764)
      "double"   in testArithmetic(select.take(_ => varint42 * double42).compile, 1764)
      "varint"   in testArithmetic(select.take(_ => varint42 * varint42).compile, 1764)
      "decimal"  in testArithmetic(select.take(_ => varint42 * decimal42).compile, 1764)
    }

    "decimal * " - {
      "tinyint"  in testArithmetic(select.take(_ => decimal42 * tinyint42).compile, 1764)
      "smallint" in testArithmetic(select.take(_ => decimal42 * smallint42).compile, 1764)
      "int"      in testArithmetic(select.take(_ => decimal42 * int42).compile, 1764)
      "bigint"   in testArithmetic(select.take(_ => decimal42 * bigint42).compile, 1764)
      "float"    in testArithmetic(select.take(_ => decimal42 * float42).compile, 1764)
      "double"   in testArithmetic(select.take(_ => decimal42 * double42).compile, 1764)
      "varint"   in testArithmetic(select.take(_ => decimal42 * varint42).compile, 1764)
      "decimal"  in testArithmetic(select.take(_ => decimal42 * decimal42).compile, 1764)
    }
  }

  "Div" - {
    "tinyint / " - {
      "tinyint"  in testArithmetic(select.take(_ => tinyint42 / tinyint42).compile, 1)
      "smallint" in testArithmetic(select.take(_ => tinyint42 / smallint42).compile, 1)
      "int"      in testArithmetic(select.take(_ => tinyint42 / int42).compile, 1)
      "bigint"   in testArithmetic(select.take(_ => tinyint42 / bigint42).compile, 1)
      "float"    in testArithmetic(select.take(_ => tinyint42 / float42).compile, 1)
      "double"   in testArithmetic(select.take(_ => tinyint42 / double42).compile, 1)
      "varint"   in testArithmetic(select.take(_ => tinyint42 / varint42).compile, 1)
      "decimal"  in testArithmetic(select.take(_ => tinyint42 / decimal42).compile, 1)
    }

    "smallint / " - {
      "tinyint"  in testArithmetic(select.take(_ => smallint42 / tinyint42).compile, 1)
      "smallint" in testArithmetic(select.take(_ => smallint42 / smallint42).compile, 1)
      "int"      in testArithmetic(select.take(_ => smallint42 / int42).compile, 1)
      "bigint"   in testArithmetic(select.take(_ => smallint42 / bigint42).compile, 1)
      "float"    in testArithmetic(select.take(_ => smallint42 / float42).compile, 1)
      "double"   in testArithmetic(select.take(_ => smallint42 / double42).compile, 1)
      "varint"   in testArithmetic(select.take(_ => smallint42 / varint42).compile, 1)
      "decimal"  in testArithmetic(select.take(_ => smallint42 / decimal42).compile, 1)
    }

    "int / " - {
      "tinyint"  in testArithmetic(select.take(_ => int42 / tinyint42).compile, 1)
      "smallint" in testArithmetic(select.take(_ => int42 / smallint42).compile, 1)
      "int"      in testArithmetic(select.take(_ => int42 / int42).compile, 1)
      "bigint"   in testArithmetic(select.take(_ => int42 / bigint42).compile, 1)
      "float"    in testArithmetic(select.take(_ => int42 / float42).compile, 1)
      "double"   in testArithmetic(select.take(_ => int42 / double42).compile, 1)
      "varint"   in testArithmetic(select.take(_ => int42 / varint42).compile, 1)
      "decimal"  in testArithmetic(select.take(_ => int42 / decimal42).compile, 1)
    }

    "bigint / " - {
      "tinyint"  in testArithmetic(select.take(_ => bigint42 / tinyint42).compile, 1)
      "smallint" in testArithmetic(select.take(_ => bigint42 / smallint42).compile, 1)
      "int"      in testArithmetic(select.take(_ => bigint42 / int42).compile, 1)
      "bigint"   in testArithmetic(select.take(_ => bigint42 / bigint42).compile, 1)
      "float"    in testArithmetic(select.take(_ => bigint42 / float42).compile, 1)
      "double"   in testArithmetic(select.take(_ => bigint42 / double42).compile, 1)
      "varint"   in testArithmetic(select.take(_ => bigint42 / varint42).compile, 1)
      "decimal"  in testArithmetic(select.take(_ => bigint42 / decimal42).compile, 1)
    }

    "float / " - {
      "tinyint"  in testArithmetic(select.take(_ => float42 / tinyint42).compile, 1)
      "smallint" in testArithmetic(select.take(_ => float42 / smallint42).compile, 1)
      "int"      in testArithmetic(select.take(_ => float42 / int42).compile, 1)
      "bigint"   in testArithmetic(select.take(_ => float42 / bigint42).compile, 1)
      "float"    in testArithmetic(select.take(_ => float42 / float42).compile, 1)
      "double"   in testArithmetic(select.take(_ => float42 / double42).compile, 1)
      "varint"   in testArithmetic(select.take(_ => float42 / varint42).compile, 1)
      "decimal"  in testArithmetic(select.take(_ => float42 / decimal42).compile, 1)
    }

    "double / " - {
      "tinyint"  in testArithmetic(select.take(_ => double42 / tinyint42).compile, 1)
      "smallint" in testArithmetic(select.take(_ => double42 / smallint42).compile, 1)
      "int"      in testArithmetic(select.take(_ => double42 / int42).compile, 1)
      "bigint"   in testArithmetic(select.take(_ => double42 / bigint42).compile, 1)
      "float"    in testArithmetic(select.take(_ => double42 / float42).compile, 1)
      "double"   in testArithmetic(select.take(_ => double42 / double42).compile, 1)
      "varint"   in testArithmetic(select.take(_ => double42 / varint42).compile, 1)
      "decimal"  in testArithmetic(select.take(_ => double42 / decimal42).compile, 1)
    }

    "varint / " - {
      "tinyint"  in testArithmetic(select.take(_ => varint42 / tinyint42).compile, 1)
      "smallint" in testArithmetic(select.take(_ => varint42 / smallint42).compile, 1)
      "int"      in testArithmetic(select.take(_ => varint42 / int42).compile, 1)
      "bigint"   in testArithmetic(select.take(_ => varint42 / bigint42).compile, 1)
      "float"    in testArithmetic(select.take(_ => varint42 / float42).compile, 1)
      "double"   in testArithmetic(select.take(_ => varint42 / double42).compile, 1)
      "varint"   in testArithmetic(select.take(_ => varint42 / varint42).compile, 1)
      "decimal"  in testArithmetic(select.take(_ => varint42 / decimal42).compile, 1)
    }

    "decimal / " - {
      "tinyint"  in testArithmetic(select.take(_ => decimal42 / tinyint42).compile, 1)
      "smallint" in testArithmetic(select.take(_ => decimal42 / smallint42).compile, 1)
      "int"      in testArithmetic(select.take(_ => decimal42 / int42).compile, 1)
      "bigint"   in testArithmetic(select.take(_ => decimal42 / bigint42).compile, 1)
      "float"    in testArithmetic(select.take(_ => decimal42 / float42).compile, 1)
      "double"   in testArithmetic(select.take(_ => decimal42 / double42).compile, 1)
      "varint"   in testArithmetic(select.take(_ => decimal42 / varint42).compile, 1)
      "decimal"  in testArithmetic(select.take(_ => decimal42 / decimal42).compile, 1)
    }
  }

  "Complex expressions" - {
    "a * b + c" in testArithmetic(select.take(_ => int42 * int42 + int42).compile, 1806)
    "(a * b) + c" in testArithmetic(select.take(_ => <<(int42 * int42) + int42).compile, 1806)
    "a * (b + c)" in testArithmetic(select.take(_ => int42 * <<(int42 + int42)).compile, 3528)
    "a * ((b + c) / d)" in testArithmetic(select.take { _ => int42 * <<(<<(int42 + int42) / int42) }.compile, 84)
  }
