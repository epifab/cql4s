package casa

import cats.effect.kernel.Resource
import cats.effect.{ExitCode, IO, IOApp}
import com.datastax.dse.protocol.internal.request.query.DseQueryOptions
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{AsyncResultSet, ResultSet, Row, Statement}
import com.datastax.oss.protocol.internal.request.query.QueryOptions

import java.net.InetSocketAddress
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletionStage
import javax.net.ssl.SSLContext
import scala.concurrent.Future
import scala.jdk.FutureConverters.*
import scala.util.Random
import scala.util.chaining.*

case class CassandraCredentials(username: String, password: String)
case class CassandraConfig(host: String, port: Int, credentials: Option[CassandraCredentials], keyspace: String, datacenter: String)

object Hello extends IOApp :

  override def run(args: List[String]): IO[ExitCode] = {
    val resultSet: fs2.Stream[IO, Unit] =
      for {
        session <-
          fs2.Stream.resource(
            Resource.make(
              IO.fromFuture(IO(CqlSession
                .builder()
                .addContactPoint(new InetSocketAddress(config.host, config.port))
                .withLocalDatacenter(config.datacenter)
                .pipe { builder => config.credentials.fold(builder)(creds => builder.withAuthCredentials(creds.username, creds.password)) }
                .withKeyspace(config.keyspace)
                .buildAsync()
                .asScala
              ))
            )(session => IO(session.close()))
          )

        _ <-
          fs2.Stream.eval(IO.fromFuture(IO(
            session
              .executeAsync(
                "INSERT INTO shopping_cart (userid, item_count, last_update_timestamp) VALUES (?, ?, ?)",
                UUID.randomUUID().toString,
                Random.between(1, 10),
                Instant.now()
              )
              .asScala
          )))

        result <-
          fs2.Stream.eval(IO.fromFuture(IO(
            session
              .executeAsync("SELECT userid, item_count, last_update_timestamp FROM shopping_cart")
              .asScala
          )))

        item <-
          fs2.Stream
            .eval(IO(Option(result.one())))
            .repeat
            .collectWhile { case Some(value) => value }
            .evalMap(row => IO(println(s"${row.getInt(1)}, ${row.getInstant(2)}")))

      } yield item

    resultSet.compile.drain.as(ExitCode.Success)
  }

  val config = CassandraConfig(host = "0.0.0.0", port = 9042, credentials = None, keyspace = "store", datacenter = "testdc")

