package cql4s

import com.datastax.oss.driver.api.core.AllNodesFailedException
import com.datastax.oss.driver.api.core.servererrors.{QueryExecutionException, QueryValidationException}

sealed abstract class CassandraException(message: String, cause: Throwable) extends RuntimeException(message, cause)

case class StatementExecutionException(cql: String, underneath: QueryExecutionException | AllNodesFailedException)
  extends CassandraException(s"Statement execution failed: ${underneath.getMessage}. Statement: $cql", underneath)

case class StatementValidationException(cql: String, underneath: QueryValidationException)
  extends CassandraException(s"Statement validation failed: ${underneath.getMessage}. Statement: $cql", underneath)

case class DriverUnknownException(cql: String, underneath: Throwable)
  extends CassandraException(s"Statement failed with an unknown exception: ${underneath.getMessage}. Statement: $cql", underneath)
