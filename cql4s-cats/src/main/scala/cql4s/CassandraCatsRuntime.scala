package cql4s

import cats.Applicative.ops.toAllApplicativeOps
import cats.effect.kernel.{Resource, Sync}
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.*
import cql4s.dsl.*
import cql4s.{CassandraConfig, CassandraRuntime}

class CassandraCatsRuntime[F[_]: Sync](protected val session: CqlSession) extends CassandraRuntime[F, [A] =>> fs2.Stream[F, A]]:
  def execute(cql: String, params: List[Any]): F[ResultSet] =
    val statement = statementBuilder(new SimpleStatementBuilder(cql), params).build()
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
      val statement = buildBatchStatement(batchType, command)(rows)
      Sync[F].blocking(session.execute(statement)).void


object CassandraCatsRuntime:
  def apply[F[_]: Sync](config: CassandraConfig): Resource[F, CassandraCatsRuntime[F]] =
    Resource.make(
      Sync[F].blocking(
        new CassandraCatsRuntime(config.getSession())
      )
    )(runtime => Sync[F].blocking(runtime.session.close()))
