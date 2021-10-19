package cql4s

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{BatchType, ResultSet, SimpleStatementBuilder}
import cql4s.dsl.{Command, Query}
import cql4s.{CassandraConfig, CassandraRuntime}
import zio.{URIO, ZIO}
import zio.stream.ZStream

class CassandraZioRuntime(protected val session: CqlSession) extends CassandraRuntime[[A] =>> ZIO[Any, Throwable, A], [A] =>> ZStream[Any, Throwable, A]]:
  def execute(cql: String, params: List[Any]): ZIO[Any, Throwable, ResultSet] =
    val statement = statementBuilder(new SimpleStatementBuilder(cql), params).build()
    ZIO.blocking(ZIO(session.execute(statement)))

  def execute[Input, Output](query: Query[Input, Output]): Input => ZStream[Any, Throwable, Output] =
    (input: Input) =>
      for {
        resultSet <- ZStream.fromZIO(execute(query.cql, query.encoder.encode(input)))
        row <- ZStream.fromZIO(ZIO.blocking(ZIO(Option(resultSet.one())))).forever.collectWhile { case Some(row) => row }
      } yield query.decoder.decode(row)

  def execute[Input](command: Command[Input]): Input => ZIO[Any, Throwable, Unit] =
    (input: Input) =>
      execute(command.cql, command.encoder.encode(input)).map(_ => ())

  def executeBatch[Input](command: Command[Input], batchType: BatchType): Iterable[Input] => ZIO[Any, Throwable, Unit] =
    (rows: Iterable[Input]) =>
      val statement = buildBatchStatement(batchType, command)(rows)
      ZIO.blocking(ZIO(session.execute(statement))).map(_ => ())


object CassandraZioRuntime:
  def apply(config: CassandraConfig): ZIO.Release[Any, Throwable, CassandraZioRuntime] =
    ZIO.acquireReleaseWith(
      ZIO.blocking(
        ZIO(new CassandraZioRuntime(config.getSession()))
      )
    )(runtime => URIO(runtime.session.close()))
