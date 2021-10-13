package cql4s

import cats.data.Chain.Singleton
import cats.effect.unsafe.IORuntime
import cats.effect.{IO, IOApp}
import com.datastax.oss.driver.api.core.cql.BatchType
import cql4s.keyspaces.Music.{Event, Metadata, events}
import cql4s.keyspaces.People.{User, Phone, Address, users}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.{Instant, LocalDate, ZoneOffset}
import java.util.{Currency, UUID}
import scala.concurrent.duration.*

class IntegrationSpec extends AnyFreeSpec with Matchers with CassandraAware:

  implicit val ioRuntime: IORuntime = IORuntime.global

  "Music keyspace" - {
    val event1 = Event(
      UUID.randomUUID(),
      "Roundhouse",
      LocalDate.now.atStartOfDay.toInstant(ZoneOffset.UTC),
      List("Radiohead", "Sigur Ros"),
      Map(
        Currency.getInstance("USD") -> BigDecimal(20.5),
        Currency.getInstance("GBP") -> BigDecimal(16)
      ),
      Set("rock", "post rock", "indie"),
      Metadata(Instant.now, None, "epifab")
    )

    val event2 = event1.copy(
      id = UUID.randomUUID(),
      startTime = LocalDate.now.plusDays(1).atStartOfDay.toInstant(ZoneOffset.UTC)
    )

    val insert: Command[Event] =
      Insert
        .into(events)
        .usingTtl(15.seconds)
        .compile
        .pcontramap[Event]

    val update: Command[(Map[Currency, BigDecimal], Metadata, UUID)] =
      Update(events)
        .set(e => (e("prices"), e("metadata")))
        .where(_("id") === :?)
        .compile

    val select: Query[UUID, Event] =
      Select
        .from(events)
        .take(_.*)
        .where(_("id") === :?)
        .compile
        .pmap[Event]

    "Insert CQL" in {
      insert.cql shouldBe "INSERT INTO music.events" +
        " (id, venue, start_time, artists, prices, tags, metadata)" +
        " VALUES (?, ?, ?, ?, ?, ?, ?)" +
        " USING TTL 15"
    }

    "Update CQL" in {
      update.cql shouldBe "UPDATE music.events" +
        " SET prices = ?, metadata = ?" +
        " WHERE id = ?"
    }

    "Select CQL" in {
      select.cql shouldBe "SELECT" +
        " id, venue, start_time, artists, prices, tags, metadata" +
        " FROM music.events" +
        " WHERE id = ?"
    }

    "Can insert / retrieve data" in {
      val now = Instant.now
      cassandraRuntime.use(cassandra =>
        for {
          _ <- cassandra.execute("TRUNCATE music.events", List.empty)
          _ <- cassandra.executeBatch(insert, BatchType.LOGGED)(List(event1, event2))
          _ <- cassandra.execute(select)(event1.id).evalTap(e => cassandra.execute(update)((
            Map(Currency.getInstance("USD") -> 32),
            e.metadata.copy(updatedAt = Some(now)),
            e.id
          ))).compile.drain
          updatedEvents <- cassandra.execute(select)(event1.id).compile.toList
        } yield updatedEvents
      ).unsafeRunSync().toSet shouldBe Set(event1.copy(
        prices = Map(Currency.getInstance("USD") -> 32),
        metadata = event1.metadata.copy(updatedAt = Some(now))
      ))
    }

  }

  "People keyspace" - {
    val insert: Command[User] =
      Insert
        .into(users)
        .compile
        .pcontramap[User]

    val select: Query[Unit, User] =
      Select
        .from(users)
        .take(_.*)
        .compile
        .pmap[User]

    "Insert and retrieve" in {
      cassandraRuntime.use { cassandra =>
        for {
          _ <- cassandra.execute(insert)(User("john", Address("Drayton Park", "London", "N5", List(Phone(44, "712345678")))))
          _ <- cassandra.execute(select)(())
            .evalTap(u => IO(println(u)))
            .compile
            .drain
        } yield ()
      }.unsafeRunSync()
    }
  }