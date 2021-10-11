package cql4s

import cats.effect.IO
import cats.effect.kernel.Resource
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.*
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
    encoded match
      case Nil => statement
      case head :: tail => buildStatement(statement.addPositionalValue(head), tail)

  def execute(cql: String, params: List[Any]): IO[ResultSet] =
    val statement = buildStatement(new SimpleStatementBuilder(cql), params).build()
    IO.blocking(session.execute(statement))

  def execute[Input, Output](query: Query[Input, Output]): Input => fs2.Stream[IO, Output] =
    (input: Input) =>
      for {
        resultSet <- fs2.Stream.eval(execute(query.cql, query.encoder.encode(input)))
        row <- fs2.Stream.eval(IO(Option(resultSet.one()))).repeat.collectWhile { case Some(row) => row }
      } yield query.decoder.decode(row)

  def execute[Input](command: Command[Input]): Input => IO[Unit] =
    (input: Input) =>
      execute(command.cql, command.encoder.encode(input)).void

  def executeBatch[Input](command: Command[Input], batchType: BatchType): Iterable[Input] => IO[Unit] =
    (rows: Iterable[Input]) =>
      val statement: BatchStatement = 
        rows
          .map(placeholders => buildStatement(new SimpleStatementBuilder(command.cql), command.encoder.encode(placeholders)).build())
          .foldLeft(new BatchStatementBuilder(batchType)) { case (batch, statement) => batch.addStatement(statement) }
          .build()
      IO.blocking(session.execute(statement)).void


object CassandraRuntime:
  def apply(config: CassandraConfig): Resource[IO, CassandraRuntime] =
    Resource.make(
      IO.blocking(
        CqlSession
          .builder()
          .addContactPoint(new InetSocketAddress(config.host, config.port))
          .withLocalDatacenter(config.datacenter)
          .pipe { builder => config.credentials.fold(builder)(creds => builder.withAuthCredentials(creds.username, creds.password)) }
          .withKeyspace(config.keyspace)
          .build()
      ).map(new CassandraRuntime(_))
    )(runtime => IO(runtime.session.close()))
