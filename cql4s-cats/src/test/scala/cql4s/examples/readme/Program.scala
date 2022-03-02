package cql4s.examples.readme

import cats.effect.{ExitCode, IO, IOApp}
import cql4s.{CassandraCatsRuntime, CassandraConfig}

import java.time.Instant
import java.util.{Currency, UUID}

object Program extends IOApp:
  val cassandraConfig = CassandraConfig(
    "0.0.0.0",
    9042,
    credentials = None,
    keyspace = None,
    datacenter = "testdc"
  )

  def run(args: List[String]): IO[ExitCode] =
    CassandraCatsRuntime[IO](cassandraConfig)
      .map(cassandra => new EventsRepo(using cassandra))
      .use { repo =>
        repo
          .findByIds(args.map(UUID.fromString))
          // Update existing event price
          .evalTap(e => repo.updateTickets(
            Map(Currency.getInstance("GBP") -> 49.99),
            e.metadata.copy(updatedAt = Some(Instant.now)),
            e.id
          ))
          // Create a new event with same artists on a different day
          .evalTap(e => repo.add(e.copy(
            id = UUID.randomUUID(),
            startTime = Instant.parse("2022-03-08T20:30:00Z"),
            metadata = e.metadata.copy(createdAt = Instant.now, updatedAt = None)
          )))
          .compile
          .drain
          .as(ExitCode.Success)
      }
