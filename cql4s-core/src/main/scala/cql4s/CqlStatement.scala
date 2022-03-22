package cql4s

import com.datastax.oss.driver.api.core.ConsistencyLevel
import com.datastax.oss.driver.api.core.cql.*
import cql4s.dsl.{Command, Query}

import scala.annotation.tailrec
import scala.util.chaining.*

private[cql4s] object CqlStatement:
  @tailrec
  private def builder(statement: SimpleStatementBuilder, encoded: List[Any]): SimpleStatementBuilder =
    encoded match
      case Nil => statement
      case head :: tail => builder(statement.addPositionalValue(head), tail)

  def apply[I, O](query: Query[I, O])(input: I): SimpleStatement =
    builder(new SimpleStatementBuilder(query.cql), query.encoder.encode(input))
      .pipe(b => query.consistencyLevel.fold(b)(b.setConsistencyLevel))
      .build()

  def apply[I](command: Command[I])(input: I): SimpleStatement =
    builder(new SimpleStatementBuilder(command.cql), command.encoder.encode(input))
      .pipe(b => command.consistencyLevel.fold(b)(b.setConsistencyLevel))
      .build()

  def apply[I](command: Command[I], batchType: BatchType)(input: Iterable[I]): BatchStatement =
    input
      .map(apply(command))
      .foldLeft(new BatchStatementBuilder(batchType)) { case (batch, statement) => batch.addStatement(statement) }
      .pipe(b => command.consistencyLevel.fold(b)(b.setConsistencyLevel))
      .build()
