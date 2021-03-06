package cql4s

import cats.effect.kernel.{Resource, Sync}
import cats.syntax.functor.toFunctorOps
import com.datastax.oss.driver.api.core.cql.*
import com.datastax.oss.driver.api.core.servererrors.{QueryExecutionException, QueryValidationException}
import com.datastax.oss.driver.api.core.{AllNodesFailedException, CqlSession}
import cql4s.dsl.*
import cql4s.{CassandraConfig, CassandraRuntimeAlgebra}

type CassandraCatsRuntimeInterpreter[F[_]] = CassandraRuntimeAlgebra[F, [A] =>> fs2.Stream[F, A]]

class CassandraCatsRuntime[F[_]: Sync](protected val session: CqlSession) extends CassandraCatsRuntimeInterpreter[F]:

  extension[A](fa: F[A])
    def remapErrors(cql: String): F[A] =
      Sync[F].handleErrorWith(fa) {
        case ex: QueryValidationException => Sync[F].raiseError(StatementValidationException(cql, ex))
        case ex: QueryExecutionException  => Sync[F].raiseError(StatementExecutionException(cql, ex))
        case ex: AllNodesFailedException  => Sync[F].raiseError(StatementExecutionException(cql, ex))
        case ex => Sync[F].raiseError(DriverUnknownException(cql, ex))
      }

  private def execute[T <: Statement[T]](statement: Statement[T]): F[ResultSet] =
    Sync[F].blocking(session.execute(statement))

  override def stream[Input, Output](query: Query[Input, Output]): Input => fs2.Stream[F, Output] =
    (input: Input) =>
      for {
        resultSet <- fs2.Stream.eval(execute(CqlStatement(query)(input)).remapErrors(query.cql))
        row <- fs2.Stream.eval(Sync[F].blocking(Option(resultSet.one()))).repeat.collectWhile { case Some(row) => row }
      } yield query.decoder.decode(row)

  override def option[Input, Output](query: Query[Input, Output]): Input => F[Option[Output]] =
    (input: Input) =>
      stream(query)(input)
        .compile
        .fold[Option[Output]](None) {
          case ((None, output)) => Some(output)
          case _ => throw new RuntimeException(s"At most 1 record expected, got many for $query")
        }

  override def one[Input, Output](query: Query[Input, Output]): Input => F[Output] =
    (input: Input) =>
      option(query)(input)
        .map(_.getOrElse(throw new RuntimeException(s"Exactly one record expected, got none for $query")))

  override def execute[Input](command: Command[Input]): Input => F[Unit] =
    (input: Input) =>
      execute(CqlStatement(command)(input)).remapErrors(command.cql).void

  override def executeBatch[Input](command: Command[Input], batchType: BatchType): Iterable[Input] => F[Unit] =
    (rows: Iterable[Input]) =>
      execute(CqlStatement(command, batchType)(rows)).remapErrors(command.cql).void


object CassandraCatsRuntime:
  def apply[F[_]: Sync](config: CassandraConfig): Resource[F, CassandraCatsRuntime[F]] =
    Resource.make(
      Sync[F].blocking(
        new CassandraCatsRuntime(config.unsafeGetSession())
      )
    )(runtime => Sync[F].blocking(runtime.session.close()))
