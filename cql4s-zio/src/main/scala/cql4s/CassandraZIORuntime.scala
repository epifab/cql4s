package cql4s

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{BatchType, ResultSet, Statement}
import cql4s.dsl.{Command, Query}
import zio.stream.ZStream
import zio.{Has, URIO, ZIO, ZLayer}

object CassandraZIORuntime extends CassandraRuntime[[A] =>> ZIO[Has[CqlSession], Throwable, A], [A] =>> ZStream[Has[CqlSession], Throwable, A]]:
  private def execute[T <: Statement[T]](statement: Statement[T]): ZIO[Has[CqlSession], Throwable, ResultSet] =
    for {
      session <- ZIO.service[CqlSession]
      resultSet <- ZIO.blocking(ZIO(session.execute(statement)))
    } yield resultSet

  override def stream[Input, Output](query: Query[Input, Output]): Input => ZStream[Has[CqlSession], Throwable, Output] =
    (input: Input) =>
      for {
        resultSet <- ZStream.fromZIO(execute(CqlStatement(query)(input)))
        row <- ZStream.fromZIO(ZIO.blocking(ZIO(Option(resultSet.one())))).forever.collectWhile { case Some(row) => row }
      } yield query.decoder.decode(row)

  override def option[Input, Output](query: Query[Input, Output]): Input => ZIO[Has[CqlSession], Throwable, Option[Output]] =
    (input: Input) =>
      stream(query)(input)
        .fold[Option[Output]](None) {
          case ((None, output)) => Some(output)
          case _ => throw new RuntimeException(s"At most 1 record expected, got many for $query")
        }

  override def one[Input, Output](query: Query[Input, Output]): Input => ZIO[Has[CqlSession], Throwable, Output] =
    (input: Input) =>
      option(query)(input)
        .map(_.getOrElse(throw new RuntimeException(s"Exactly one record expected, got none for $query")))

  override def execute[Input](command: Command[Input]): Input => ZIO[Has[CqlSession], Throwable, Unit] =
    (input: Input) =>
      execute(CqlStatement(command)(input)).map(_ => ())

  override def executeBatch[Input](command: Command[Input], batchType: BatchType): Iterable[Input] => ZIO[Has[CqlSession], Throwable, Unit] =
    (rows: Iterable[Input]) =>
      execute(CqlStatement(batchType, command)(rows)).map(_ => ())

  def session(config: CassandraConfig): ZLayer[Any, Throwable, Has[CqlSession]] = {
    val acquire = ZIO.blocking(ZIO(config.getSession()))
    ZLayer.fromAcquireRelease(acquire)(session => URIO(session.close()))
  }
