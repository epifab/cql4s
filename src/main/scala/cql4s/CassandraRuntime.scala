package cql4s

import cats.Applicative.ops.toAllApplicativeOps
import cats.effect.kernel.{Resource, Sync}
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

case class CassandraConfig(
  host: String,
  port: Int,
  credentials: Option[CassandraCredentials],
  keyspace: Option[String],
  datacenter: String
)

class CassandraRuntime[F[_]: Sync](protected val session: CqlSession):
  @tailrec
  private def buildStatement(statement: SimpleStatementBuilder, encoded: List[Any]): SimpleStatementBuilder =
    encoded match
      case Nil => statement
      case head :: tail => buildStatement(statement.addPositionalValue(head), tail)

  def execute(cql: String, params: List[Any]): F[ResultSet] =
    val statement = buildStatement(new SimpleStatementBuilder(cql), params).build()
    Sync[F].blocking(session.execute(statement))

  def execute[Input, Output](query: Query[Input, Output]): Input => fs2.Stream[F, Output] =
    (input: Input) =>
      for {
        resultSet <- fs2.Stream.eval(execute(query.cql, query.encoder.encode(input)))
        row <- fs2.Stream.eval(Sync[F].blocking(Option(resultSet.one()))).repeat.collectWhile { case Some(row) => row }
      } yield query.decoder.decode(row)

  def execute[Input](command: Command[Input]): Input => F[Unit] =
    (input: Input) =>
      execute(command.cql, command.encoder.encode(input)).void

  def executeBatch[Input](command: Command[Input], batchType: BatchType): Iterable[Input] => F[Unit] =
    (rows: Iterable[Input]) =>
      val statement: BatchStatement = 
        rows
          .map(placeholders => buildStatement(new SimpleStatementBuilder(command.cql), command.encoder.encode(placeholders)).build())
          .foldLeft(new BatchStatementBuilder(batchType)) { case (batch, statement) => batch.addStatement(statement) }
          .build()
      Sync[F].blocking(session.execute(statement)).void


object CassandraRuntime:
  def apply[F[_]: Sync](config: CassandraConfig): Resource[F, CassandraRuntime[F]] =
    Resource.make(
      Sync[F].blocking(
        CqlSession
          .builder()
          .addContactPoint(new InetSocketAddress(config.host, config.port))
          .withLocalDatacenter(config.datacenter)
          .pipe { builder => config.credentials.fold(builder)(creds => builder.withAuthCredentials(creds.username, creds.password)) }
          .pipe { builder => config.keyspace.fold(builder)(keyspace => builder.withKeyspace(keyspace)) }
          .build()
      ).map(new CassandraRuntime(_))
    )(runtime => Sync[F].blocking(runtime.session.close()))
