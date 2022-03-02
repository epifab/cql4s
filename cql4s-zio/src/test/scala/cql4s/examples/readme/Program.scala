package cql4s.examples.readme

import cql4s.{CassandraConfig, CassandraZIORuntime, CassandraZLayer}
import zio.{Has, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.time.Instant
import java.util.{Currency, UUID}

object Program extends ZIOAppDefault:
  val cassandraLayer = CassandraZLayer(CassandraConfig(
    "0.0.0.0",
    9042,
    credentials = None,
    keyspace = None,
    datacenter = "testdc"
  ))

  val repo = new EventsRepo(using CassandraZIORuntime)

  val program = ZIO.service[ZIOAppArgs].flatMap(args =>
    repo
      .findByIds(args.getArgs.toList.map(UUID.fromString))
      // Update existing event price
      .tap(e => repo.updateTickets(
        Map(Currency.getInstance("GBP") -> 49.99),
        e.metadata.copy(updatedAt = Some(Instant.now)),
        e.id
      ))
      // Create a new event with same artists on a different day
      .tap(e => repo.add(
        e.copy(
          id = UUID.randomUUID(),
          startTime = Instant.parse("2022-03-08T20:30:00Z"),
          metadata = e.metadata.copy(createdAt = Instant.now, updatedAt = None)
        )
      ))
      .runDrain
  )

  val run = program.provideSomeLayer(cassandraLayer)
