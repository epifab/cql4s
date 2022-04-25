package cql4s.test

import cql4s.test.model.*

import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDate, ZoneOffset}
import java.util.{Currency, UUID}

object fixtures:
  val atSomePoint = Instant.now.truncatedTo(ChronoUnit.MILLIS)

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
    Metadata(atSomePoint, None, User("epifab", Some("info@epifab.solutions"), (44, "12345678")))
  )

  val event2 = event1.copy(
    id = UUID.randomUUID(),
    startTime = LocalDate.now.plusDays(1).atStartOfDay.toInstant(ZoneOffset.UTC)
  )
