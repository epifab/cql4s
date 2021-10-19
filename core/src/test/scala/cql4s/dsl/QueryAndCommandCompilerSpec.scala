package cql4s.dsl

import com.datastax.oss.driver.api.core.cql.BatchType
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import cql4s.test.queries.*

import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDate, ZoneOffset}
import java.util.{Currency, UUID}

class QueryAndCommandCompilerSpec extends AnyFreeSpec with Matchers:
  "Insert CQL" in {
    insertEvent.cql shouldBe "INSERT INTO music.events" +
      " (id, venue, start_time, artists, tickets, tags, metadata)" +
      " VALUES (?, ?, ?, ?, ?, ?, ?)" +
      " USING TTL 15"
  }

  "Update CQL" in {
    updateEventTickets.cql shouldBe "UPDATE music.events" +
      " SET tickets = ?, metadata = ?" +
      " WHERE id = ?"
  }

  "Select CQL" in {
    findEventById.cql shouldBe "SELECT" +
      " id, venue, start_time, artists, tickets, tags, metadata" +
      " FROM music.events" +
      " WHERE id = ?"
  }
