package cql4s

import cats.Applicative.ops.toAllApplicativeOps
import cats.effect.kernel.{Resource, Sync}
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.*
import cql4s.dsl.*
import cql4s.{CassandraConfig, CassandraRuntime}

class CassandraCatsRuntime[F[_]: Sync](protected val session: CqlSession) extends CassandraRuntime[F, [A] =>> fs2.Stream[F, A]]:
  private def execute[T <: Statement[T]](statement: Statement[T]): F[ResultSet] =
    Sync[F].blocking(session.execute(statement))

  def execute[Input, Output](query: Query[Input, Output]): Input => fs2.Stream[F, Output] =
    (input: Input) =>
      for {
        resultSet <- fs2.Stream.eval(execute(CqlStatement(query)(input)))
        row <- fs2.Stream.eval(Sync[F].blocking(Option(resultSet.one()))).repeat.collectWhile { case Some(row) => row }
      } yield query.decoder.decode(row)

  def execute[Input](command: Command[Input]): Input => F[Unit] =
    (input: Input) =>
      execute(command)(input).void

  def executeBatch[Input](command: Command[Input], batchType: BatchType): Iterable[Input] => F[Unit] =
    (rows: Iterable[Input]) =>
      execute(CqlStatement(batchType, command)(rows)).void


object CassandraCatsRuntime:
  def apply[F[_]: Sync](config: CassandraConfig): Resource[F, CassandraCatsRuntime[F]] =
    Resource.make(
      Sync[F].blocking(
        new CassandraCatsRuntime(config.getSession())
      )
    )(runtime => Sync[F].blocking(runtime.session.close()))
