package cql4s

import com.datastax.oss.driver.api.core.cql.*
import cql4s.dsl.{Command, Query}

import scala.annotation.tailrec

private[cql4s] object CqlStatement:
  @tailrec
  private def builder(statement: SimpleStatementBuilder, encoded: List[Any]): SimpleStatementBuilder =
    encoded match
      case Nil => statement
      case head :: tail => builder(statement.addPositionalValue(head), tail)

  def apply[I, O](query: Query[I, O])(input: I): SimpleStatement =
    builder(new SimpleStatementBuilder(query.cql), query.encoder.encode(input)).build()

  def apply[I](command: Command[I])(input: I): SimpleStatement =
    builder(new SimpleStatementBuilder(command.cql), command.encoder.encode(input)).build()

  def apply[I](batchType: BatchType, command: Command[I])(input: Iterable[I]): BatchStatement =
    input
      .map(apply(command))
      .foldLeft(new BatchStatementBuilder(batchType)) { case (batch, statement) => batch.addStatement(statement) }
      .build()
