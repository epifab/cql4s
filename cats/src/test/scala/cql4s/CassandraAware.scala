package cql4s

import cats.effect.IO
import cats.effect.kernel.Resource
import cql4s.CassandraCatsRuntime

trait CassandraAware:

  private val config = CassandraConfig(
    host = "0.0.0.0",
    port = 9042,
    credentials = None,
    keyspace = None,
    datacenter = "testdc"
  )

  protected val cassandraRuntime: Resource[IO, CassandraCatsRuntime[IO]] = CassandraCatsRuntime[IO](config)
