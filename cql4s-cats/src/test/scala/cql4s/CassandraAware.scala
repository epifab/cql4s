package cql4s

import cats.effect.IO
import cats.effect.kernel.Deferred
import cats.effect.unsafe.IORuntime
import cql4s.test.CassandraTestConfig
import org.scalatest.{BeforeAndAfterAll, Suite}

trait CassandraAware extends BeforeAndAfterAll:
  this: Suite =>
    given IORuntime = IORuntime.global

    private val cassandraAndShutdownHandler: Deferred[IO, (CassandraCatsRuntime[IO], IO[Unit])] = Deferred.unsafe
    protected val cassandra: IO[CassandraCatsRuntime[IO]] = cassandraAndShutdownHandler.get.map(_(0))

    override def beforeAll(): Unit = {
      super.beforeAll()
      (for {
        c <- CassandraCatsRuntime[IO](CassandraTestConfig).allocated
        _ <- cassandraAndShutdownHandler.complete(c)
      } yield ()).unsafeRunSync()
    }

    override def afterAll(): Unit = {
      super.afterAll()
      cassandraAndShutdownHandler.get.flatMap(_(1)).unsafeRunSync()
    }
