package casa

import casa.compiler.Query
import cats.arrow.FunctionK
import cats.effect.IO
import cats.effect.kernel.Resource
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{SimpleStatementBuilder, StatementBuilder}
import com.datastax.oss.driver.api.core.session.Session

import java.net.InetSocketAddress
import scala.annotation.tailrec
import scala.collection.JavaConverters.*
import scala.concurrent.ExecutionContext
import scala.jdk.FutureConverters.*
import scala.util.chaining.*

case class CassandraCredentials(username: String, password: String)
case class CassandraConfig(host: String, port: Int, credentials: Option[CassandraCredentials], keyspace: String, datacenter: String)

class CassandraRuntime(protected val session: CqlSession):
  @tailrec
  private def buildStatement(statement: SimpleStatementBuilder, encoded: List[Any]): SimpleStatementBuilder =
    encoded match {
      case Nil => statement
      case head :: tail => buildStatement(statement.addPositionalValue(head), tail)
    }

  def execute[Input, Output](query: Query[Input, Output]): Input => fs2.Stream[IO, Output] =
    (input: Input) => {
      val statement = buildStatement(new SimpleStatementBuilder(query.csql), query.encoder.encode(input)).build()

      for {
        resultSet <-
          fs2.Stream
            .eval(IO.fromFuture(IO(session.executeAsync(statement).asScala)))

        row <-
          fs2.Stream
            .eval(IO(Option(resultSet.one())))
            .repeat
            .collectWhile { case Some(row) => row }

      } yield query.decoder.decode(row)
    }

object CassandraRuntime:
  def apply(config: CassandraConfig): Resource[IO, CassandraRuntime] =
    Resource.make(
      IO.fromFuture(IO(
        CqlSession
          .builder()
          .addContactPoint(new InetSocketAddress(config.host, config.port))
          .withLocalDatacenter(config.datacenter)
          .pipe { builder => config.credentials.fold(builder)(creds => builder.withAuthCredentials(creds.username, creds.password)) }
          .withKeyspace(config.keyspace)
          .buildAsync()
          .asScala
        )
      ).map(new CassandraRuntime(_))
    )(runtime => IO(runtime.session.close()))
