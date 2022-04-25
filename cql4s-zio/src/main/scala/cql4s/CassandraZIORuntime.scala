package cql4s

import com.datastax.oss.driver.api.core.{AllNodesFailedException, CqlSession}
import com.datastax.oss.driver.api.core.cql.{BatchType, ResultSet, Statement}
import com.datastax.oss.driver.api.core.servererrors.{QueryExecutionException, QueryValidationException}
import cql4s.dsl.{Command, Query}
import zio.stream.ZStream
import zio.{URIO, ZIO, ZLayer, Scope}

type CassandraZIORuntimeInterpreter =
  CassandraRuntimeAlgebra[
    [A] =>> ZIO[CqlSession, CassandraException, A],
    [A] =>> ZStream[CqlSession, CassandraException, A]
  ]

given cassandraZIORuntimeInterpreter: CassandraZIORuntimeInterpreter = CassandraZIORuntime

object CassandraZIORuntime extends CassandraZIORuntimeInterpreter:

  private def execute[T <: Statement[T]](cql: String, statement: Statement[T]): ZIO[CqlSession, CassandraException, ResultSet] =
    for {
      session <- ZIO.service[CqlSession]
      resultSet <- ZIO.blocking(ZIO.attempt(session.execute(statement)).catchAll {
        case ex: QueryValidationException => ZIO.fail(StatementValidationException(cql, ex))
        case ex: QueryExecutionException  => ZIO.fail(StatementExecutionException(cql, ex))
        case ex: AllNodesFailedException  => ZIO.fail(StatementExecutionException(cql, ex))
        case ex => ZIO.fail(DriverUnknownException(cql, ex))
      })
    } yield resultSet

  override def stream[Input, Output](query: Query[Input, Output]): Input => ZStream[CqlSession, CassandraException, Output] =
    (input: Input) =>
      for {
        resultSet <- ZStream.fromZIO(execute(query.cql, CqlStatement(query)(input)))
        row <- ZStream.fromZIO(ZIO.succeedBlocking(Option(resultSet.one()))).forever.collectWhile { case Some(row) => row }
      } yield query.decoder.decode(row)

  override def option[Input, Output](query: Query[Input, Output]): Input => ZIO[CqlSession, CassandraException, Option[Output]] =
    (input: Input) =>
      stream(query)(input)
        .runFold[Option[Output]](None) {
          case ((None, output)) => Some(output)
          case _ => throw new RuntimeException(s"At most 1 record expected, got many for $query")
        }

  override def one[Input, Output](query: Query[Input, Output]): Input => ZIO[CqlSession, CassandraException, Output] =
    (input: Input) =>
      option(query)(input)
        .map(_.getOrElse(throw new RuntimeException(s"Exactly one record expected, got none for $query")))

  override def execute[Input](command: Command[Input]): Input => ZIO[CqlSession, CassandraException, Unit] =
    (input: Input) =>
      execute(command.cql, CqlStatement(command)(input)).map(_ => ())

  override def executeBatch[Input](command: Command[Input], batchType: BatchType): Iterable[Input] => ZIO[CqlSession, CassandraException, Unit] =
    (rows: Iterable[Input]) =>
      execute(command.cql, CqlStatement(command, batchType)(rows)).map(_ => ())


object CassandraZLayer:
  def apply(config: CassandraConfig): ZLayer[Scope, Throwable, CqlSession] =
    ZLayer(ZIO.acquireRelease(ZIO.blocking(ZIO.attempt(config.unsafeGetSession())))(s => ZIO.succeed(s.close())))
