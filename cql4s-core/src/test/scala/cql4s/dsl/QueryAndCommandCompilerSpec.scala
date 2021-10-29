package cql4s.dsl

import com.datastax.oss.driver.api.core.cql.BatchType
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import cql4s.test.queries.*
import cql4s.test.schema.*

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
    findEventsById.cql shouldBe "SELECT" +
      " id, venue, start_time, artists, tickets, tags, metadata" +
      " FROM music.events" +
      " WHERE id IN ?"
  }

  "Delete CQL" in {
    deleteEvent.cql shouldBe "DELETE FROM music.events" +
      " WHERE id = ?"
  }

  "Select" - {
    val baseQuery = Select.from(events)

    "nothing" in {
      baseQuery
        .compile
        .cql shouldBe "SELECT 1 FROM music.events"
    }

    "one field" in {
      baseQuery
        .take(_("id"))
        .compile
        .cql shouldBe "SELECT id FROM music.events"
    }

    "two fields" in {
      baseQuery
        .take(e => (e("id"), e("tickets")))
        .compile
        .cql shouldBe "SELECT id, tickets FROM music.events"
    }

    "where" - {
      "id = ?" in {
        baseQuery
          .where(_("id") === :?)
          .compile
          .cql shouldBe "SELECT 1 FROM music.events WHERE id = ?"
      }

      "id = id" in {
        baseQuery
          .where(e => e("id") === e("id"))
          .compile
          .cql shouldBe "SELECT 1 FROM music.events WHERE id = id"
      }

      "id = ? and venue = ?" in {
        baseQuery
          .where(e => (e("id") === :?) and (e("venue") === :?))
          .compile
          .cql shouldBe "SELECT 1 FROM music.events WHERE id = ? AND venue = ?"
      }

      "id = ? or venue = ?" in {
        baseQuery
          .where(e => (e("id") === :?) or (e("venue") === :?))
          .compile
          .cql shouldBe "SELECT 1 FROM music.events WHERE id = ? OR venue = ?"
      }

      "id = ? or venue = ? and start_time = ?" in {
        baseQuery
          .where(e => (e("id") === :?) or (e("venue") === :?) and e("start_time") === :?)
          .compile
          .cql shouldBe "SELECT 1 FROM music.events WHERE id = ? OR venue = ? AND start_time = ?"
      }

      "id = ? or (venue = ? and start_time = ?)" in {
        baseQuery
          .where(e => (e("id") === :?) or <<((e("venue") === :?) and (e("start_time") === :?)))
          .compile
          .cql shouldBe "SELECT 1 FROM music.events WHERE id = ? OR (venue = ? AND start_time = ?)"
      }

      "(id = ? or venue = ?) and start_time = ?" in {
        baseQuery
          .where(e => <<((e("id") === :?) or (e("venue") === :?)) and (e("start_time") === :?))
          .compile
          .cql shouldBe "SELECT 1 FROM music.events WHERE (id = ? OR venue = ?) AND start_time = ?"
      }
    }

    "group by" - {
      "1 field" in {
        baseQuery
          .groupBy(_("venue"))
          .compile
          .cql shouldBe "SELECT 1 FROM music.events GROUP BY venue"
      }

      "2 fields" in {
        baseQuery
          .groupBy(x => (x("venue"), x("tickets")))
          .compile
          .cql shouldBe "SELECT 1 FROM music.events GROUP BY venue, tickets"
      }
    }

    "order by" - {
      "one field (default)" in {
        baseQuery
          .orderBy(_("start_time"))
          .compile
          .cql shouldBe "SELECT 1 FROM music.events ORDER BY start_time"
      }

      "one field (ascending)" in {
        baseQuery
          .orderBy(_("start_time").asc)
          .compile
          .cql shouldBe "SELECT 1 FROM music.events ORDER BY start_time ASC"
      }

      "one field (descending)" in {
        baseQuery
          .orderBy(_("start_time").desc)
          .compile
          .cql shouldBe "SELECT 1 FROM music.events ORDER BY start_time DESC"
      }

      "two fields" in {
        baseQuery
          .orderBy(e => (e("start_time").desc, e("venue")))
          .compile
          .cql shouldBe "SELECT 1 FROM music.events ORDER BY start_time DESC, venue"
      }
    }

    "limit" in {
      baseQuery
        .limit(10L[bigint])
        .compile
        .cql shouldBe "SELECT 1 FROM music.events LIMIT ?"
    }

    "per partition limit" in {
      baseQuery
        .perPartitionLimit(10L[bigint])
        .compile
        .cql shouldBe "SELECT 1 FROM music.events PER PARTITION LIMIT ?"
    }
  }
