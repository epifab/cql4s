package cql4s

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{BatchStatementBuilder, BatchType, SimpleStatement, SimpleStatementBuilder}
import cql4s.dsl.{Command, Query}

import java.net.InetSocketAddress
import scala.annotation.tailrec
import scala.util.chaining.*

case class CassandraCredentials(username: String, password: String)

case class CassandraConfig(
  host: String,
  port: Int,
  credentials: Option[CassandraCredentials],
  keyspace: Option[String],
  datacenter: String
):
  protected [cql4s] def getSession(): CqlSession =
    CqlSession.builder()
      .addContactPoint(new InetSocketAddress(host, port))
      .withLocalDatacenter(datacenter)
      .pipe { builder => credentials.fold(builder)(creds => builder.withAuthCredentials(creds.username, creds.password)) }
      .pipe { builder => keyspace.fold(builder)(keyspace => builder.withKeyspace(keyspace)) }
      .build()

trait CassandraRuntime[F[_], S[_]]:
  def execute[Input, Output](query: Query[Input, Output]): Input => S[Output]
  def execute[Input](command: Command[Input]): Input => F[Unit]

  @tailrec
  protected final def statementBuilder(statement: SimpleStatementBuilder, encoded: List[Any]): SimpleStatementBuilder =
    encoded match
      case Nil => statement
      case head :: tail => statementBuilder(statement.addPositionalValue(head), tail)

  protected def buildStatement[I, O](query: Query[I, O])(input: I): SimpleStatement =
    statementBuilder(new SimpleStatementBuilder(query.cql), query.encoder.encode(input)).build()

  protected def buildStatement[I](command: Command[I])(input: I): SimpleStatement =
    statementBuilder(new SimpleStatementBuilder(command.cql), command.encoder.encode(input)).build()

  protected def buildBatchStatement[I](batchType: BatchType, command: Command[I])(input: Iterable[I]) =
    input
      .map(buildStatement(command))
      .foldLeft(new BatchStatementBuilder(batchType)) { case (batch, statement) => batch.addStatement(statement) }
      .build()
