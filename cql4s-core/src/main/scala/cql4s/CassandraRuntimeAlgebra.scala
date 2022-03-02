package cql4s

import com.datastax.oss.driver.api.core.AllNodesFailedException
import com.datastax.oss.driver.api.core.cql.BatchType
import com.datastax.oss.driver.api.core.servererrors.{QueryExecutionException, QueryValidationException}
import cql4s.dsl.{Command, Query}

trait CassandraRuntimeAlgebra[F[_], S[_]]:
  def stream[Input, Output](query: Query[Input, Output]): Input => S[Output]
  def option[Input, Output](query: Query[Input, Output]): Input => F[Option[Output]]
  def one[Input, Output](query: Query[Input, Output]): Input => F[Output]
  def execute[Input](command: Command[Input]): Input => F[Unit]
  def executeBatch[Input](command: Command[Input], batchType: BatchType): Iterable[Input] => F[Unit]
