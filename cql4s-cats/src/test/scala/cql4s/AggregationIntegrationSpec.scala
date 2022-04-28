package cql4s

import cats.effect.IO
import cql4s.dsl.*
import cql4s.test.model.{Event, Metadata, User}
import org.scalatest.Assertion
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Instant, LocalDate, LocalTime, ZoneOffset}
import java.util.UUID

class AggregationIntegrationSpec extends AnyFreeSpec with Matchers with CassandraAware:
  import cql4s.test.schema.dummy

  val insertText: Command[Option[String]] =
    Insert
      .into(dummy)
      .fields(d => (d("id"), d("_text")))
      .compile
      .contramap[Option[String]](s => (UUID.randomUUID(), s))

  val truncate = Truncate(dummy).compile

  "count(*), count(id)" in {
    val countStar = Select
      .from(dummy)
      .take(_ => count(*))
      .where(_("_text") === :?)
      .allowFiltering(true)
      .compile

    val countText = Select
      .from(dummy)
      .take(d => count(d("_text")))
      .allowFiltering(true)
      .compile

    (for {
      given _ <- cassandra

      _ <- truncate.execute(())
      _ <- insertText.execute(Some("match"))
      _ <- insertText.execute(Some("match"))
      _ <- insertText.execute(None)

      result1 <- countStar.one(Some("match"))
      result2 <- countText.one(())
      result3 <- countStar.one(Some("matching-nothing"))
    } yield (result1, result2, result3)).unsafeRunSync() shouldBe (2L, 2L, 0L)
  }

  "max, min" in {
    val maxText = Select
      .from(dummy)
      .take(e => max(e("_text")))
      .compile

    val minText = Select
      .from(dummy)
      .take(e => min(e("_text")))
      .compile

    (for {
      given _ <- cassandra

      _ <- truncate.execute(())
      _ <- insertText.execute(Some("a"))
      _ <- insertText.execute(Some("b"))
      _ <- insertText.execute(None)

      result1 <- minText.one(())
      result2 <- maxText.one(())
    } yield (result1, result2)).unsafeRunSync() shouldBe (Some("a"), Some("b"))
  }

  {
    val insert: Command[Int] =
      Insert
        .into(dummy)
        .fields(d => (
          d("id"),
          d("_tinyint"),
          d("_smallint"),
          d("_int"),
          d("_bigint"),
          d("_float"),
          d("_double"),
          d("_varint"),
          d("_decimal")
        ))
        .compile
        .contramap[Int](i => (
          UUID.randomUUID(),
          Some(i.toByte),
          Some(i.toShort),
          Some(i),
          Some(i.toLong),
          Some(i.toFloat),
          Some(i.toDouble),
          Some(BigInt(i)),
          Some(BigDecimal(i))
        ))

    def test[A](a: Query[Unit, A]): A =
      (for {
        given _ <- cassandra

        _ <- truncate.execute(())
        _ <- insert.execute(10)
        _ <- insert.execute(15)
        a <- a.one(())
      } yield a).unsafeRunSync()

    val select = Select.from(dummy)

    "avg" - {
      "tinyint" in {
        test(select.take(d => avg(d("_tinyint"))).compile) shouldBe Some(12.toByte)
      }
      "smallint" in {
        test(select.take(d => avg(d("_smallint"))).compile) shouldBe Some(12.toShort)
      }
      "int" in {
        test(select.take(d => avg(d("_int"))).compile) shouldBe Some(12)
      }
      "bigint" in {
        test(select.take(d => avg(d("_bigint"))).compile) shouldBe Some(12L)
      }
      "float" in {
        test(select.take(d => avg(d("_float"))).compile) shouldBe Some(12.5.toFloat)
      }
      "double" in {
        test(select.take(d => avg(d("_double"))).compile) shouldBe Some(12.5)
      }
      "varint" in {
        test(select.take(d => avg(d("_varint"))).compile) shouldBe Some(BigInt(12))
      }
      // avg of a decimal field surprisingly returns an integer.
      // what is even weirder, is that the rounding seems to be non deterministic, it returns either 12 or 13
      // this test is therefore ignored
      "decimal" ignore {
        test(select.take(d => avg(d("_decimal"))).compile) shouldBe Some(BigDecimal(12))
      }
    }

    "sum" - {
      "tinyint" in {
        test(select.take(d => sum(d("_tinyint"))).compile) shouldBe Some(25.toByte)
      }
      "smallint" in {
        test(select.take(d => sum(d("_smallint"))).compile) shouldBe Some(25.toShort)
      }
      "int" in {
        test(select.take(d => sum(d("_int"))).compile) shouldBe Some(25)
      }
      "bigint" in {
        test(select.take(d => sum(d("_bigint"))).compile) shouldBe Some(25L)
      }
      "float" in {
        test(select.take(d => sum(d("_float"))).compile) shouldBe Some(25.toFloat)
      }
      "double" in {
        test(select.take(d => sum(d("_double"))).compile) shouldBe Some(25.toDouble)
      }
      "varint" in {
        test(select.take(d => sum(d("_varint"))).compile) shouldBe Some(BigInt(25))
      }
      "decimal" in {
        test(select.take(d => sum(d("_decimal"))).compile) shouldBe Some(BigDecimal(25))
      }
    }
  }
